package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.data.AbyssWrenchBehaviorData.AbyssWrenchBehavior;
import com.karasu256.projectk.energy.EnergyKeys;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.block.IEnergyBlock;
import net.karasuniki.karasunikilib.api.data.impl.EnergyValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AbyssEnergyCableBlockEntity extends BlockEntity implements ICableInputable, ICableOutputable {
    private static final WeakHashMap<Level, Long> LAST_TICK = new WeakHashMap<>();
    private static final WeakHashMap<Level, Set<BlockPos>> PROCESSED = new WeakHashMap<>();
    private final EnergyValue energy = new EnergyValue();
    private long capacity;
    private long transferRate;
    private final EnumMap<Direction, AbyssWrenchBehavior> behaviors = new EnumMap<>(Direction.class);

    public AbyssEnergyCableBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ENERGY_CABLE.get(), pos, state);
        this.capacity = resolveCapacity(state);
        this.transferRate = resolveTransferRate(state);
        energy.setCapacity(capacity);
        energy.setValue(0);
        energy.setId(null);
        for (Direction direction : Direction.values()) {
            behaviors.put(direction, AbyssWrenchBehavior.NORMAL);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssEnergyCableBlockEntity be) {
        if (level.isClientSide) {
            return;
        }
        be.serverTick();
    }

    private void serverTick() {
        if (level == null) {
            return;
        }
        long gameTime = level.getGameTime();
        Set<BlockPos> processed = PROCESSED.computeIfAbsent(level, key -> new HashSet<>());
        Long lastTick = LAST_TICK.get(level);
        if (lastTick == null || lastTick != gameTime) {
            processed.clear();
            LAST_TICK.put(level, gameTime);
        }
        if (processed.contains(worldPosition)) {
            return;
        }

        NetworkState network = collectNetwork();
        processed.addAll(network.cables);
        if (network.mixedEnergy) {
            return;
        }

        pullFromAcceptors(network);
        pushToAcceptors(network);
        saveNetworkBuffer(network);
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        return insert(id, maxAmount, simulate, null);
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        AbyssWrenchBehavior behavior = side == null ? AbyssWrenchBehavior.NORMAL : getBehavior(side);
        if (behavior == AbyssWrenchBehavior.OUTPUT || behavior == AbyssWrenchBehavior.NONE) {
            return 0;
        }
        if (energy.getValue() > 0 && energy.getId() != null && !energy.getId().equals(id)) {
            return 0;
        }
        long accepted = Math.min(maxAmount, capacity - energy.getValue());
        if (!simulate && accepted > 0) {
            if (energy.getValue() == 0 || energy.getId() == null) {
                energy.setId(id);
            }
            energy.setValue(energy.getValue() + accepted);
            setChanged();
        }
        return accepted;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        return extract(id, maxAmount, simulate, null);
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        AbyssWrenchBehavior behavior = side == null ? AbyssWrenchBehavior.NORMAL : getBehavior(side);
        if (behavior == AbyssWrenchBehavior.INPUT || behavior == AbyssWrenchBehavior.NONE) {
            return 0;
        }
        if (energy.getId() == null || !energy.getId().equals(id)) {
            return 0;
        }
        long extracted = Math.min(energy.getValue(), maxAmount);
        if (!simulate && extracted > 0) {
            energy.setValue(energy.getValue() - extracted);
            if (energy.getValue() == 0) {
                energy.setId(null);
            }
            setChanged();
        }
        return extracted;
    }

    private NetworkState collectNetwork() {
        NetworkState state = new NetworkState();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(worldPosition);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!state.cables.add(pos)) {
                continue;
            }
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof AbyssEnergyCableBlockEntity cable)) {
                continue;
            }
            state.capacity += cable.getEnergyCapacity();
            long value = cable.getEnergyAmount();
            if (value > 0) {
                ResourceLocation id = cable.energy.getId();
                if (state.energyId == null) {
                    state.energyId = id;
                } else if (!state.energyId.equals(id)) {
                    state.mixedEnergy = true;
                }
                state.energy += value;
            }
            BlockState blockState = level.getBlockState(pos);
            for (Direction dir : Direction.values()) {
                if (!blockState.getValue(AbyssEnergyCable.getConnectionPropertyFor(dir))) {
                    continue;
                }
                BlockPos neighborPos = pos.relative(dir);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                if (neighbor instanceof AbyssEnergyCableBlockEntity) {
                    queue.add(neighborPos);
                } else {
                    state.acceptors.add(new AcceptorRef(pos, dir, neighbor));
                }
            }
        }
        return state;
    }

    private void pullFromAcceptors(NetworkState network) {
        if (network.capacity <= 0) {
            return;
        }
        if (transferRate <= 0) {
            return;
        }
        long available = Math.min(network.capacity - network.energy, transferRate);
        if (available <= 0) {
            return;
        }
        for (AcceptorRef ref : network.acceptors) {
            if (!(ref.neighbor instanceof ICableOutputable outputable)) {
                continue;
            }
            BlockEntity cableBe = level.getBlockEntity(ref.cablePos);
            if (!(cableBe instanceof AbyssEnergyCableBlockEntity cable)) {
                continue;
            }
            AbyssWrenchBehavior behavior = cable.getBehavior(ref.direction);
            if (behavior != AbyssWrenchBehavior.INPUT && behavior != AbyssWrenchBehavior.NORMAL) {
                continue;
            }

            ResourceLocation energyId = resolveEnergyIdForPull(ref.neighbor, network.energyId, network.energy);
            if (energyId == null) {
                continue;
            }
            Direction neighborSide = ref.direction.getOpposite();
            long extracted = outputable.extract(energyId, available, true, neighborSide);
            if (extracted <= 0) {
                continue;
            }
            long accepted = Math.min(extracted, available);
            outputable.extract(energyId, accepted, false, neighborSide);
            if (network.energyId == null) {
                network.energyId = energyId;
            }
            network.energy += accepted;
            available -= accepted;
            if (available <= 0) {
                break;
            }
        }
    }

    private void pushToAcceptors(NetworkState network) {
        if (network.energy <= 0 || network.energyId == null) {
            return;
        }
        if (transferRate <= 0) {
            return;
        }
        long available = Math.min(network.energy, transferRate);
        if (available <= 0) {
            return;
        }

        List<TransferTarget> targets = new ArrayList<>();
        long totalAccept = 0;
        for (AcceptorRef ref : network.acceptors) {
            if (!(ref.neighbor instanceof ICableInputable inputable)) {
                continue;
            }
            BlockEntity cableBe = level.getBlockEntity(ref.cablePos);
            if (!(cableBe instanceof AbyssEnergyCableBlockEntity cable)) {
                continue;
            }
            AbyssWrenchBehavior behavior = cable.getBehavior(ref.direction);
            if (behavior != AbyssWrenchBehavior.OUTPUT && behavior != AbyssWrenchBehavior.NORMAL) {
                continue;
            }
            Direction neighborSide = ref.direction.getOpposite();
            long accept = inputable.insert(network.energyId, available, true, neighborSide);
            if (accept <= 0) {
                continue;
            }
            targets.add(new TransferTarget(ref.cablePos, ref.direction, neighborSide, inputable, accept));
            totalAccept += accept;
        }
        if (totalAccept <= 0) {
            return;
        }

        long remaining = available;
        for (TransferTarget target : targets) {
            if (remaining <= 0) {
                break;
            }
            long share = (available * target.accept) / totalAccept;
            if (share <= 0) {
                continue;
            }
            long sent = sendToTarget(network.energyId, target, Math.min(share, remaining));
            remaining -= sent;
        }

        if (remaining > 0) {
            for (TransferTarget target : targets) {
                if (remaining <= 0) {
                    break;
                }
                long extra = Math.min(target.accept, remaining);
                long sent = sendToTarget(network.energyId, target, extra);
                remaining -= sent;
            }
        }

        network.energy -= (available - remaining);
    }

    private long sendToTarget(ResourceLocation energyId, TransferTarget target, long amount) {
        if (amount <= 0) {
            return 0;
        }
        long accepted = target.inputable.insert(energyId, amount, true, target.neighborSide);
        if (accepted <= 0) {
            return 0;
        }
        BlockEntity be = level.getBlockEntity(target.cablePos);
        if (!(be instanceof AbyssEnergyCableBlockEntity cable)) {
            return 0;
        }
        long extracted = cable.extract(energyId, accepted, false, target.direction);
        if (extracted <= 0) {
            return 0;
        }
        target.inputable.insert(energyId, extracted, false, target.neighborSide);
        return extracted;
    }

    private void saveNetworkBuffer(NetworkState network) {
        int count = network.cables.size();
        if (count <= 0) {
            return;
        }
        long remaining = Math.max(0L, network.energy);
        long perCable = remaining / count;
        long extra = remaining % count;
        for (BlockPos pos : network.cables) {
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof AbyssEnergyCableBlockEntity cable)) {
                continue;
            }
            long value = perCable + (extra-- > 0 ? 1 : 0);
            cable.setEnergyInternal(network.energyId, value);
        }
    }

    private ResourceLocation resolveEnergyIdForPull(BlockEntity neighbor, ResourceLocation networkId, long networkAmount) {
        if (networkAmount > 0 && networkId != null) {
            return networkId;
        }
        if (neighbor instanceof IEnergyBlock<?> energyBlock) {
            if (energyBlock.getAmount() <= 0) {
                return null;
            }
            return energyBlock.getEnergyType().getId();
        }
        return null;
    }

    public AbyssWrenchBehavior getBehavior(Direction side) {
        return side == null ? AbyssWrenchBehavior.NORMAL : behaviors.getOrDefault(side, AbyssWrenchBehavior.NORMAL);
    }

    public void setBehavior(Direction side, AbyssWrenchBehavior behavior) {
        if (side == null) {
            return;
        }
        AbyssWrenchBehavior next = behavior == null ? AbyssWrenchBehavior.NORMAL : behavior;
        behaviors.put(side, next);
        setChanged();
        syncToClient();
    }

    public void setBehavior(AbyssWrenchBehavior behavior) {
        AbyssWrenchBehavior next = behavior == null ? AbyssWrenchBehavior.NORMAL : behavior;
        for (Direction direction : Direction.values()) {
            behaviors.put(direction, next);
        }
        setChanged();
        syncToClient();
    }

    private void setEnergyInternal(ResourceLocation energyId, long value) {
        if (value <= 0) {
            energy.setValue(0);
            energy.setId(null);
        } else {
            if (energyId != null) {
                energy.setId(energyId);
            }
            energy.setValue(value);
        }
        setChanged();
        syncToClient();
    }

    public long getEnergyAmount() {
        return energy.getValue();
    }

    public long getEnergyCapacity() {
        return capacity;
    }

    public com.karasu256.projectk.energy.IProjectKEnergy getEnergyType() {
        return energy instanceof com.karasu256.projectk.energy.IProjectKEnergy pkEnergy ? pkEnergy : null;
    }

    private static final class NetworkState {
        private final Set<BlockPos> cables = new HashSet<>();
        private final List<AcceptorRef> acceptors = new ArrayList<>();
        private ResourceLocation energyId;
        private long energy;
        private long capacity;
        private boolean mixedEnergy;
    }

    private static final class AcceptorRef {
        private final BlockPos cablePos;
        private final Direction direction;
        private final BlockEntity neighbor;

        private AcceptorRef(BlockPos cablePos, Direction direction, BlockEntity neighbor) {
            this.cablePos = cablePos;
            this.direction = direction;
            this.neighbor = neighbor;
        }
    }

    private static final class TransferTarget {
        private final BlockPos cablePos;
        private final Direction direction;
        private final Direction neighborSide;
        private final ICableInputable inputable;
        private final long accept;

        private TransferTarget(BlockPos cablePos, Direction direction, Direction neighborSide, ICableInputable inputable, long accept) {
            this.cablePos = cablePos;
            this.direction = direction;
            this.neighborSide = neighborSide;
            this.inputable = inputable;
            this.accept = accept;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        CompoundTag behaviorTag = new CompoundTag();
        for (Map.Entry<Direction, AbyssWrenchBehavior> entry : behaviors.entrySet()) {
            behaviorTag.putString(entry.getKey().getSerializedName(), entry.getValue().id());
        }
        nbt.put(EnergyKeys.CABLE_BEHAVIORS.toString(), behaviorTag);
        energy.writeNbt(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        CompoundTag behaviorTag = nbt.getCompound(EnergyKeys.CABLE_BEHAVIORS.toString());
        for (Direction direction : Direction.values()) {
            behaviors.put(direction, AbyssWrenchBehavior.fromString(behaviorTag.getString(direction.getSerializedName())));
        }
        energy.readNbt(nbt, registries);
        if (energy.getValue() <= 0) {
            energy.setId(null);
        }
        this.capacity = resolveCapacity(getBlockState());
        this.transferRate = resolveTransferRate(getBlockState());
        energy.setCapacity(capacity);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        CompoundTag behaviorTag = new CompoundTag();
        for (Map.Entry<Direction, AbyssWrenchBehavior> entry : behaviors.entrySet()) {
            behaviorTag.putString(entry.getKey().getSerializedName(), entry.getValue().id());
        }
        nbt.put(EnergyKeys.CABLE_BEHAVIORS.toString(), behaviorTag);
        energy.writeNbt(nbt, registries);
        return nbt;
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssEnergyCable cable) {
            return cable.getCapacity();
        }
        return 0L;
    }

    private long resolveTransferRate(BlockState state) {
        if (state.getBlock() instanceof AbyssEnergyCable cable) {
            return cable.getTransferRate();
        }
        return 0L;
    }

    private void syncToClient() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }
}
