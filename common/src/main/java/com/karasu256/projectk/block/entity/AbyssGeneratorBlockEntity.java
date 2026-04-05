package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbstractEnergyBlock;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.IEnergyListHolder;
import com.karasu256.projectk.energy.IMaxEnrgyInfo;
import com.karasu256.projectk.energy.ITierInfo;
import com.karasu256.projectk.exception.EnergyNotFound;
import com.karasu256.projectk.particle.IAbssParticleMoveable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AbyssGeneratorBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> implements IEnergyListHolder, IAbssParticleMoveable, IMaxEnrgyInfo, ITierInfo {
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;
    private final long baseMaxEnergy;
    private long maxEnergy;
    private int tier;

    public AbyssGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_GENERATOR.get(), pos, state, resolveCapacity(state));
        this.baseMaxEnergy = resolveCapacity(state);
        this.tier = DEFAULT_TIER;
        refreshMaxEnergy();
    }

    private static long resolveCapacity(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof AbstractEnergyBlock energyBlock) {
            return energyBlock.getEnergyProperties().energyCapacity();
        }
        throw new EnergyNotFound(state.getBlock());
    }

    @Override
    protected AbyssEnergy createEnergy() {
        return new AbyssEnergy(0L);
    }

    private void refreshMaxEnergy() {
        setMaxEnergy(getTieredMaxEnergy(getTier()));
        setMaxEnergyCapacity(getMaxEnergy());
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
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = clampTier(tier);
        refreshMaxEnergy();
        setChanged();
        sync();
    }

    @Override
    public int getMaxTier() {
        return MAX_TIER;
    }

    @Override
    public int getDefaultTier() {
        return DEFAULT_TIER;
    }

    @Override
    public List<EnergyEntry> getEnergyEntries() {
        ResourceLocation id = getAbyssEnergyId();
        if (id == null || getAmount() <= 0) {
            return List.of();
        }
        return List.of(new EnergyEntry(id, getAmount(), getCapacity(), true));
    }

    @Override
    protected boolean canOutputEnergy() {
        return true;
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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        saveAdditional(nbt, registries);
        return nbt;
    }
}
