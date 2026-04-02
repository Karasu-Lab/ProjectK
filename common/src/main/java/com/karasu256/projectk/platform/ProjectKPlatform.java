package com.karasu256.projectk.platform;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public interface ProjectKPlatform {
    LiquidBlock createFluidBlock(RegistrySupplier<FlowingFluid> fluid, BlockBehaviour.Properties properties);
}
