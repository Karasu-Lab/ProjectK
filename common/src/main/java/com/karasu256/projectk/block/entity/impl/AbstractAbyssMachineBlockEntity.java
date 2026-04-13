package com.karasu256.projectk.block.entity.impl;

import com.karasu256.projectk.api.machine.IMachineCapacity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.IMultiEnergyStorage;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.karasuniki.karasunikilib.api.data.impl.HeldItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAbyssMachineBlockEntity extends AbstractAbyssTieredBlockEntity implements IMultiEnergyStorage, ICableInputable, ICableOutputable, ICapacity {
    protected final List<AbyssEnergyData> energies = new ArrayList<>();
    protected final List<HeldItem> heldItems = new ArrayList<>();
    protected final int maxEnergyTypes;

    protected AbstractAbyssMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RegistrySupplier<? extends IMachineCapacity> capacitySupplier) {
        this(type, pos, state, capacitySupplier, 1);
    }

    protected AbstractAbyssMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, ProjectKMachineCapacities.NONE);
    }

    protected AbstractAbyssMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RegistrySupplier<? extends IMachineCapacity> capacitySupplier, int maxEnergyTypes) {
        super(type, pos, state, capacitySupplier);
        this.maxEnergyTypes = maxEnergyTypes;
    }

    protected void addItemSlot(ResourceLocation id) {
        heldItems.add(new HeldItem(id));
    }

    @Override
    public List<AbyssEnergyData> getEnergyList() {
        return energies;
    }

    @Override
    public Long getEnergyCapacity() {
        return getMaxEnergy() < 0 ? null : getMaxEnergy();
    }

    @Override
    public int getMaxEnergyTypes() {
        return maxEnergyTypes;
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0) {
            return 0;
        }
        int index = findEnergyIndex(id);
        if (index < 0 && getEnergyTypeCount() >= getMaxEnergyTypes()) {
            return 0;
        }
        long current = index >= 0 ? energies.get(index).amountOrZero() : 0L;
        long received;
        if (getMaxEnergy() < 0) {
            received = maxAmount;
        } else {
            received = Math.min(getMaxEnergy() - current, maxAmount);
        }

        if (received <= 0) {
            return 0;
        }
        if (!simulate) {
            long nextAmount = current + received;
            if (index >= 0) {
                energies.set(index, new AbyssEnergyData(id, nextAmount));
            } else {
                energies.add(new AbyssEnergyData(id, nextAmount));
            }
            markDirtyAndSync();
        }
        return received;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0) {
            return 0;
        }
        int index = findEnergyIndex(id);
        if (index < 0) {
            return 0;
        }
        long current = energies.get(index).amountOrZero();
        long extracted = Math.min(current, maxAmount);
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            long remaining = current - extracted;
            if (remaining <= 0) {
                energies.remove(index);
            } else {
                energies.set(index, new AbyssEnergyData(id, remaining));
            }
            markDirtyAndSync();
        }
        return extracted;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        if (side != null && !canOutputEnergy()) {
            return 0;
        }
        return extract(id, maxAmount, simulate);
    }

    protected boolean canOutputEnergy() {
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        writeEnergyListNbt(nbt);
        if (!heldItems.isEmpty()) {
            ListTag listTag = new ListTag();
            for (HeldItem item : heldItems) {
                CompoundTag slotTag = new CompoundTag();
                item.writeNbt(slotTag, registries);
                listTag.add(slotTag);
            }
            nbt.put("held_items", listTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        readEnergyListNbt(nbt);
        if (nbt.contains("held_items", Tag.TAG_LIST)) {
            ListTag listTag = nbt.getList("held_items", Tag.TAG_COMPOUND);
            for (int i = 0; i < Math.min(listTag.size(), heldItems.size()); i++) {
                heldItems.get(i).readNbt(listTag.getCompound(i), registries);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        saveAdditional(nbt, registries);
        return nbt;
    }

    @Override
    protected void resolveMaxEnergy() {
        super.resolveMaxEnergy();
        capEnergies();
    }

    protected void capEnergies() {
        long max = getMaxEnergy();
        if (max < 0 || energies == null)
            return;
        for (int i = 0; i < energies.size(); i++) {
            AbyssEnergyData data = energies.get(i);
            if (data.amountOrZero() > max) {
                energies.set(i, new AbyssEnergyData(data.energyId(), max));
            }
        }
        markDirtyAndSync();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public long getEnergyAmount() {
        return energies.isEmpty() ? 0L : energies.getFirst().amountOrZero();
    }

    @Override
    public long getCapacity() {
        return getMaxEnergy();
    }

    public long getEnergyAmount(ResourceLocation id) {
        int index = findEnergyIndex(id);
        return index >= 0 ? energies.get(index).amountOrZero() : 0L;
    }

    @Override
    @Nullable
    public ResourceLocation getAbyssEnergyId() {
        return energies.isEmpty() ? null : energies.getFirst().energyId();
    }

    public boolean canAcceptEnergy(ResourceLocation id) {
        if (id == null)
            return false;
        int index = findEnergyIndex(id);
        if (index >= 0)
            return true;
        return getEnergyTypeCount() < getMaxEnergyTypes();
    }

    public void loadFromStack(ItemStack stack) {
        energies.clear();
        AbyssEnergyData data = stack.get(
                com.karasu256.projectk.data.ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (data != null && data.energyId() != null) {
            energies.add(data);
        }
        List<AbyssEnergyData> list = AbyssEnergyData.readEnergyList(stack);
        for (AbyssEnergyData d : list) {
            if (energies.stream().noneMatch(e -> e.energyId().equals(d.energyId()))) {
                energies.add(d);
            }
        }
        markDirtyAndSync();
    }

    public void applyDropData(ItemStack stack) {
        if (energies.isEmpty()) {
            return;
        }
        for (AbyssEnergyData data : energies) {
            AbyssEnergyData.applyToStack(stack, data.energyId(), data.amount());
        }
    }
}
