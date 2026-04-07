package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.custom.AbyssEnergyCable.ConnectionMode;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AbyssEnergyCableBlockEntity extends AbstractAbyssMachineBlockEntity implements ICableInputable, ICableOutputable {
    private static final WeakHashMap<Level, Long> LAST_TICK = new WeakHashMap<>();
    private static final WeakHashMap<Level, Set<BlockPos>> PROCESSED = new WeakHashMap<>();
    private final ConnectionMode[] sideModes = new ConnectionMode[6];
    private long transferRate;

    public AbyssEnergyCableBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ENERGY_CABLE.get(), pos, state, resolveCapacity(state));
        this.transferRate = resolveTransferRate(state);
        Arrays.fill(sideModes, ConnectionMode.CONNECTED);
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
    public long insert(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        ConnectionMode mode = getModeForSide(side);
        if (mode == ConnectionMode.INPUT || mode == ConnectionMode.NONE) {
            return 0;
        }
        return insert(id, maxAmount, simulate);
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        ConnectionMode mode = getModeForSide(side);
        if (mode == ConnectionMode.OUTPUT || mode == ConnectionMode.NONE) {
            return 0;
        }
        return extract(id, maxAmount, simulate);
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
                ResourceLocation id = cable.getAbyssEnergyId();
                if (state.energyId == null) {
                    state.energyId = id;
                } else if (!state.energyId.equals(id)) {
                    state.mixedEnergy = true;
                }
                state.energy += value;
            }
            for (Direction dir : Direction.values()) {
                ConnectionMode mode = cable.getModeForSide(dir);
                if (!isCableConnected(mode)) {
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
        if (network.capacity <= 0 || transferRate <= 0) {
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
            ConnectionMode mode = cable.getModeForSide(ref.direction);
            if (mode != ConnectionMode.OUTPUT && mode != ConnectionMode.CONNECTED) {
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
        if (network.energy <= 0 || network.energyId == null || transferRate <= 0) {
            return;
        }
        long available = Math.min(network.energy, transferRate);

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
            ConnectionMode mode = cable.getModeForSide(ref.direction);
            if (mode != ConnectionMode.INPUT && mode != ConnectionMode.CONNECTED) {
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
        if (neighbor instanceof AbstractAbyssMachineBlockEntity machine) {
            return machine.getAbyssEnergyId();
        }
        return null;
    }

    public ConnectionMode getModeForSide(@Nullable Direction side) {
        if (side == null) {
            return ConnectionMode.CONNECTED;
        }
        return sideModes[side.ordinal()];
    }

    public void setModeForSide(Direction side, ConnectionMode mode) {
        sideModes[side.ordinal()] = mode;
        setChanged();
        BlockState state = AbyssEnergyCable.updateConnections(level, worldPosition, getBlockState());
        level.setBlock(worldPosition, state, 3);
        syncToClient();
    }

    private boolean isCableConnected(ConnectionMode mode) {
        return mode != ConnectionMode.NONE;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        CompoundTag modesTag = new CompoundTag();
        for (int i = 0; i < 6; i++) {
            modesTag.putString("side" + i, sideModes[i].getSerializedName());
        }
        nbt.put("modes", modesTag);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.transferRate = resolveTransferRate(getBlockState());
        if (nbt.contains("modes")) {
            CompoundTag modesTag = nbt.getCompound("modes");
            for (int i = 0; i < 6; i++) {
                String modeName = modesTag.getString("side" + i);
                sideModes[i] = parseMode(modeName);
            }
        }
    }

    private static ConnectionMode parseMode(String name) {
        for (ConnectionMode mode : ConnectionMode.values()) {
            if (mode.getSerializedName().equals(name)) {
                return mode;
            }
        }
        return ConnectionMode.NONE;
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssEnergyCable cable) {
            return cable.getCapacity();
        }
        return 0L;
    }

    private static long resolveTransferRate(BlockState state) {
        if (state.getBlock() instanceof AbyssEnergyCable cable) {
            return cable.getTransferRate();
        }
        return 0L;
    }

    private void setEnergyInternal(ResourceLocation energyId, long value) {
        if (value <= 0) {
            getEnergyList().clear();
        } else {
            if (energyId != null) {
                int index = findEnergyIndex(energyId);
                if (index >= 0) {
                    getEnergyList().set(index, new AbyssEnergyData(energyId, value));
                } else {
                    getEnergyList().clear();
                    getEnergyList().add(new AbyssEnergyData(energyId, value));
                }
            } else {
                getEnergyList().clear();
            }
        }
        setChanged();
    }

    private void syncToClient() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }

    private static final class NetworkState {
        private final Set<BlockPos> cables = new HashSet<>();
        private final List<AcceptorRef> acceptors = new ArrayList<>();
        private ResourceLocation energyId;
        private long energy;
        private long capacity;
        private boolean mixedEnergy;
    }

    private record AcceptorRef(BlockPos cablePos, Direction direction, BlockEntity neighbor) {
    }

    private record TransferTarget(BlockPos cablePos, Direction direction, Direction neighborSide,
                                  ICableInputable inputable, long accept) {
    }
}
