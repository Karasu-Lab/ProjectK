package com.karasu256.projectk.block.entity.impl;

import com.karasu256.projectk.block.custom.AbyssStorage;
import com.karasu256.projectk.block.custom.AbyssSynthesizer;
import com.karasu256.projectk.block.custom.ProjectKBlock;
import com.karasu256.projectk.block.custom.ProjectKBlock.ITieredMachineProperties;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.IMaxEnergyInfo;
import com.karasu256.projectk.energy.ITierInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

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
    public int getMaxTier() {
        return getTieredProperties().map(ITieredMachineProperties::maxTier).orElse(1);
    }

    @Override
    public int getDefaultTier() {
        return getTieredProperties().map(ITieredMachineProperties::defaultTier).orElse(1);
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
        double scaling = getTieredProperties().map(ITieredMachineProperties::capacityScaling).orElse(2.5);
        return (long) (base * Math.pow(scaling, safeTier - 1));
    }

    protected Optional<ITieredMachineProperties> getTieredProperties() {
        if (getBlockState().getBlock() instanceof ProjectKBlock pkBlock) {
            return Optional.of(pkBlock.getCustomProperties());
        }
        if (getBlockState().getBlock() instanceof AbyssSynthesizer synthesizer) {
            return Optional.of(synthesizer.getCustomProperties());
        }
        if (getBlockState().getBlock() instanceof AbyssStorage storage) {
            return Optional.of(storage.getTieredProperties());
        }
        return Optional.empty();
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
        refreshMaxEnergy();
        loadMaxEnergy(nbt);
    }
}
