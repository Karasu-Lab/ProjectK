package com.karasu256.projectk.fabric.client;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import com.karasu256.projectk.registry.EnergiesRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.karasuniki.karasunikilib.api.client.registry.KClientScanner;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;

public class ProjectkFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProjectKClient.init();
        KClientScanner.scanAll();

        ModelLoadingPlugin.register(pluginContext -> {
            Set<ResourceLocation> registeredModels = new HashSet<>();
            for (var energy : EnergiesRegistry.getEntries()) {
                ResourceLocation baseId = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/abyss_generator/" + energy.getId().getPath());
                if (registeredModels.add(baseId)) {
                    pluginContext.addModels(baseId);
                }
            }
        });

        BlockRenderLayerMap.INSTANCE.putBlock(ProjectKBlocks.ABYSS_GENERATOR.get(), RenderType.cutout());

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
}
