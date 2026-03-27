package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> implements IAbyssEnergy {
    public AbyssGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.ABYSS_GENERATOR.get(), pos, state, 10000);
    }

    @Override
    public AbyssEnergy getEnergyType() {
        return new AbyssEnergy(energy.getValue());
    }

    @Override
    public ResourceLocation getId() {
        return Id.id("abyss_energy");
    }
}
