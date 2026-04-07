package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbstractEnergyBlock;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.particle.IAbyssParticleMoveable;
import com.karasu256.projectk.exception.EnergyNotFound;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntity extends AbstractAbyssMachineBlockEntity implements IAbyssParticleMoveable {
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;

    public AbyssGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_GENERATOR.get(), pos, state, resolveCapacity(state));
    }

    private static long resolveCapacity(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof AbstractEnergyBlock energyBlock) {
            return energyBlock.getEnergyProperties().energyCapacity();
        }
        throw new EnergyNotFound(state.getBlock());
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
    protected boolean canOutputEnergy() {
        return true;
    }

    @Override
    public long getCapacity() {
        return getEnergyCapacity();
    }
}