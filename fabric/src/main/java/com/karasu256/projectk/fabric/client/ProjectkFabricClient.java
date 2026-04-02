package com.karasu256.projectk.fabric.client;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import com.karasu256.projectk.client.screen.AbyssMagicTableScreen;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.menu.ProjectKMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ProjectkFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProjectKClient.init();
        MenuScreens.register(ProjectKMenus.ABYSS_MAGIC_TABLE.get(), AbyssMagicTableScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ProjectKBlocks.ABYSS_GENERATOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ProjectKBlocks.FLUID_ABYSS_ENERGY.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ProjectKBlocks.FLUID_YIN_ABYSS_ENERGY.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ProjectKBlocks.FLUID_YANG_ABYSS_ENERGY.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),
            ProjectKFluids.ABYSS_ENERGY.get(),
            ProjectKFluids.FLOWING_ABYSS_ENERGY.get(),
            ProjectKFluids.YIN_ABYSS_ENERGY.get(),
            ProjectKFluids.FLOWING_YIN_ABYSS_ENERGY.get(),
            ProjectKFluids.YANG_ABYSS_ENERGY.get(),
            ProjectKFluids.FLOWING_YANG_ABYSS_ENERGY.get());

        registerFluidRender(ProjectKFluids.ABYSS_ENERGY.get(), ProjectKFluids.FLOWING_ABYSS_ENERGY.get(), "fluid_abyss_energy");
        registerFluidRender(ProjectKFluids.YIN_ABYSS_ENERGY.get(), ProjectKFluids.FLOWING_YIN_ABYSS_ENERGY.get(), "fluid_yin_abyss_energy");
        registerFluidRender(ProjectKFluids.YANG_ABYSS_ENERGY.get(), ProjectKFluids.FLOWING_YANG_ABYSS_ENERGY.get(), "fluid_yang_abyss_energy");

        CoreShaderRegistrationCallback.EVENT.register(ctx -> {
            ProjectKCoreShaders.init((id, format, onLoaded) -> {
                try {
                    ctx.register(id, format, onLoaded);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        });

        ProjectKClient.initLate();
    }

    private static void registerFluidRender(Fluid source, Fluid flowing, String textureBase) {
        ResourceLocation still = ResourceLocation.fromNamespaceAndPath("projectk", "block/" + textureBase + "_still");
        ResourceLocation flow = ResourceLocation.fromNamespaceAndPath("projectk", "block/" + textureBase + "_flow");
        FluidRenderHandlerRegistry.INSTANCE.register(source, flowing, new SimpleFluidRenderHandler(still, flow));
    }
}
