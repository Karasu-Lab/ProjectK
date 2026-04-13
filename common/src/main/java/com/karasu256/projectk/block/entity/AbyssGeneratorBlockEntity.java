package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.particle.IAbyssParticleMoveable;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntity extends AbstractAbyssMachineBlockEntity implements IAbyssParticleMoveable {
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;

    public AbyssGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_GENERATOR.get(), pos, state, ProjectKMachineCapacities.ABYSS_GENERATOR);
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