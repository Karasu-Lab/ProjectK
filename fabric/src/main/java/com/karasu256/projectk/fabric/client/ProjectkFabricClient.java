package com.karasu256.projectk.fabric.client;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.RenderType;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ProjectkFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProjectKClient.init();
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
