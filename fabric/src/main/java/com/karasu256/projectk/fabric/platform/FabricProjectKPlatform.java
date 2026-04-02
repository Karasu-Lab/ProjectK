package com.karasu256.projectk.fabric.platform;

import com.karasu256.projectk.platform.ProjectKPlatform;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public final class FabricProjectKPlatform implements ProjectKPlatform {
    @Override
    public LiquidBlock createFluidBlock(RegistrySupplier<FlowingFluid> fluid, BlockBehaviour.Properties properties) {
        return new FabricProjectKLiquidBlock(fluid.get(), properties);
    }

    private static final class FabricProjectKLiquidBlock extends LiquidBlock {
        private FabricProjectKLiquidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
            super(fluid, properties);
        }
    }
}
