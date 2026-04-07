package com.karasu256.projectk.block.entity.impl;

import com.karasu256.projectk.energy.IMaxEnergyInfo;
import com.karasu256.projectk.energy.ITierInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractAbyssTieredBlockEntity extends AbstractAbyssNbtBlockEntity implements ITierInfo, IMaxEnergyInfo {
    protected int tier;
    protected long maxEnergy;
    protected final long baseMaxEnergy;

    public AbstractAbyssTieredBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long baseMaxEnergy) {
        super(type, pos, state);
        this.baseMaxEnergy = baseMaxEnergy;
        this.tier = getDefaultTier();
        refreshMaxEnergy();
    }

    protected void refreshMaxEnergy() {
        this.maxEnergy = getTieredMaxEnergy(tier);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = clampTier(tier);
        refreshMaxEnergy();
        markDirtyAndSync();
    }

    @Override
    public long getBaseMaxEnergy() {
        return baseMaxEnergy;
    }

    @Override
    public long getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
        markDirtyAndSync();
    }

    @SuppressWarnings("NonStrictComparisonCanBeEquality")
    @Override
    public long getTieredMaxEnergy(int tier) {
        int safeTier = Math.max(1, tier);
        if (safeTier <= 1) {
            return getBaseMaxEnergy();
        }
        long base = getBaseMaxEnergy();
        if (base < 0) {
            return base;
        }
        return (long) (base * Math.pow(2.5, safeTier - 1));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        saveTier(nbt);
        saveMaxEnergy(nbt);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        loadTier(nbt);
        loadMaxEnergy(nbt);
        refreshMaxEnergy();
    }
}
