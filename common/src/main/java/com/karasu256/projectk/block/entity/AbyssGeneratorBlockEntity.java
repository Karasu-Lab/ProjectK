package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbstractEnergyBlock;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.exception.EnergyNotFound;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> {
    public AbyssGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_GENERATOR.get(), pos, state, resolveCapacity(state));
    }

    private static long resolveCapacity(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof AbstractEnergyBlock energyBlock) {
            return energyBlock.getEnergyProperties().getEnergyCapacity();
        }
        throw new EnergyNotFound(state.getBlock());
    }

    @Override
    protected AbyssEnergy createEnergy() {
        return new AbyssEnergy(0L);
    }
}
