package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbstractEnergyBlock;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.IEnergyListHolder;
import com.karasu256.projectk.exception.EnergyNotFound;
import com.karasu256.projectk.particle.IAbssParticleMoveable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AbyssGeneratorBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> implements IEnergyListHolder, IAbssParticleMoveable {
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
    protected AbyssEnergy createEnergy() {
        return new AbyssEnergy(0L);
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
}
