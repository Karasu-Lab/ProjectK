package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.energy.ICapacity;
import com.karasu256.projectk.energy.IEnergy;
import com.karasu256.projectk.energy.IEnergyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractEnergyBlockEntity<T extends IEnergy> extends AbstractContainerBlockEntity implements IEnergyBlock<T>, ICapacity {
    protected long energy;
    protected final long capacity;
    protected static final String ENERGY_TAG = "Energy";

    public AbstractEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state);
        this.capacity = capacity;
    }

    @Override
    public long insert(long maxAmount, boolean simulate) {
        long received = Math.min(capacity - energy, maxAmount);
        if (!simulate) {
            energy += received;
            setChanged();
            sync();
        }
        return received;
    }

    @Override
    public long extract(long maxAmount, boolean simulate) {
        long extracted = Math.min(energy, maxAmount);
        if (!simulate) {
            energy -= extracted;
            setChanged();
            sync();
        }
        return extracted;
    }

    @Override
    public long getAmount() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putLong(ENERGY_TAG, energy);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        energy = nbt.getLong(ENERGY_TAG);
    }
}
