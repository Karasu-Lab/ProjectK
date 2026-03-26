package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.karasulab.karasucore.api.data.IEnergy;
import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntity extends AbstractEnergyBlockEntity<AbyssEnergy> implements IAbyssEnergy {
    public AbyssGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.ABYSS_GENERATOR.get(), pos, state, 10000);
    }

    @Override
    public AbyssEnergy getEnergyType() {
        return new AbyssEnergy(energy);
    }
}
