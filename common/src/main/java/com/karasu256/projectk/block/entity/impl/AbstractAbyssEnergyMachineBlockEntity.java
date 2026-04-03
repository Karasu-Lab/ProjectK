package com.karasu256.projectk.block.entity.impl;

import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.karasuniki.karasunikilib.api.data.impl.EnergyValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAbyssEnergyMachineBlockEntity extends KarasuCoreBlockEntity implements ICableInputable, ICableOutputable, ICapacity {
    protected final EnergyValue energyOne = new EnergyValue();
    protected final EnergyValue energyTwo = new EnergyValue();
    protected long capacity;

    protected AbstractAbyssEnergyMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state);
        this.capacity = capacity;
    }

    protected int maxEnergyTypes() {
        return 1;
    }

    protected boolean canOutputEnergy() {
        return false;
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0) {
            return 0;
        }
        EnergyValue slot = findInsertSlot(id);
        if (slot == null) {
            return 0;
        }
        long received = Math.min(capacity - slot.getValue(), maxAmount);
        if (received <= 0) {
            return 0;
        }
        if (!simulate) {
            if (slot.getValue() == 0) {
                slot.setId(id);
            }
            slot.setValue(slot.getValue() + received);
            setChanged();
            sync();
        }
        return received;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0) {
            return 0;
        }
        EnergyValue slot = findExactSlot(id);
        if (slot == null) {
            return 0;
        }
        long extracted = Math.min(slot.getValue(), maxAmount);
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            slot.setValue(slot.getValue() - extracted);
            setChanged();
            sync();
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

    @Override
    public long getCapacity() {
        return capacity;
    }

    public ResourceLocation getEnergyId1() {
        return energyOne.getValue() == 0 ? null : energyOne.getId();
    }

    public ResourceLocation getEnergyId2() {
        return energyTwo.getValue() == 0 ? null : energyTwo.getId();
    }

    public long getEnergyAmount1() {
        return energyOne.getValue();
    }

    public long getEnergyAmount2() {
        return energyTwo.getValue();
    }

    @Nullable
    protected EnergyValue findExactSlot(ResourceLocation id) {
        if (energyOne.getValue() > 0 && id.equals(energyOne.getId())) {
            return energyOne;
        }
        if (energyTwo.getValue() > 0 && id.equals(energyTwo.getId())) {
            return energyTwo;
        }
        return null;
    }

    @Nullable
    protected EnergyValue findInsertSlot(ResourceLocation id) {
        EnergyValue exact = findExactSlot(id);
        if (exact != null) {
            return exact;
        }
        int types = countEnergyTypes();
        if (types >= maxEnergyTypes()) {
            return null;
        }
        if (energyOne.getValue() == 0) {
            return energyOne;
        }
        if (energyTwo.getValue() == 0) {
            return energyTwo;
        }
        return null;
    }

    protected long getEnergyAmount(ResourceLocation id) {
        EnergyValue slot = findExactSlot(id);
        return slot == null ? 0L : slot.getValue();
    }

    protected void writeEnergyNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        energyOne.setCapacity(capacity);
        energyTwo.setCapacity(capacity);
        CompoundTag energyOneTag = new CompoundTag();
        CompoundTag energyTwoTag = new CompoundTag();
        energyOne.writeNbt(energyOneTag, registries);
        energyTwo.writeNbt(energyTwoTag, registries);
        nbt.put("energy_one", energyOneTag);
        nbt.put("energy_two", energyTwoTag);
    }

    protected void readEnergyNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        if (nbt.contains("energy_one")) {
            energyOne.readNbt(nbt.getCompound("energy_one"), registries);
        }
        if (nbt.contains("energy_two")) {
            energyTwo.readNbt(nbt.getCompound("energy_two"), registries);
        }
    }

    private int countEnergyTypes() {
        int count = 0;
        if (energyOne.getValue() > 0) {
            count++;
        }
        if (energyTwo.getValue() > 0) {
            count++;
        }
        return count;
    }
}
