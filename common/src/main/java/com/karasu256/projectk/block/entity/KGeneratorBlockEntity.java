package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class KGeneratorBlockEntity extends BlockEntity implements IAbyssEnergy {
    private long energy;
    private final long capacity = 10000;
    private static final String ENERGY_TAG = "Energy";

    public KGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.K_GENERATOR.get(), pos, state);
    }

    @Override
    public long insert(long maxAmount, boolean simulate) {
        long received = Math.min(capacity - energy, maxAmount);
        if (!simulate) {
            energy += received;
            setChanged();
        }
        return received;
    }

    @Override
    public long extract(long maxAmount, boolean simulate) {
        long extracted = Math.min(energy, maxAmount);
        if (!simulate) {
            energy -= extracted;
            setChanged();
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
    public AbyssEnergy getAbyssEnergy() {
        return new AbyssEnergy(energy);
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
