package com.karasu256.projectk.neoforge.platform;

import com.karasu256.projectk.platform.ProjectKPlatform;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public final class NeoForgeProjectKPlatform implements ProjectKPlatform {
    @Override
    public LiquidBlock createFluidBlock(RegistrySupplier<FlowingFluid> fluid, BlockBehaviour.Properties properties) {
        return new NeoForgeProjectKLiquidBlock(fluid.get(), properties);
    }

    private static final class NeoForgeProjectKLiquidBlock extends LiquidBlock {
        private NeoForgeProjectKLiquidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
            super(fluid, properties);
        }
    }
}
