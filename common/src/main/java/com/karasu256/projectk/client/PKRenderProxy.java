package com.karasu256.projectk.client;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class PKRenderProxy {
    private PKRenderProxy() {
    }

    @FunctionalInterface
    public interface PKRenderTypeRegistrar {
        void register(RegistrySupplier<? extends Block> block, RenderType type);

        default void registerFluid(RegistrySupplier<? extends Fluid> fluid, RenderType type) {
        }
    }

    @FunctionalInterface
    public interface PKFluidRenderingRegistrar {
        void register(RegistrySupplier<? extends Fluid> source, RegistrySupplier<? extends Fluid> flowing, int color);
    }
}
