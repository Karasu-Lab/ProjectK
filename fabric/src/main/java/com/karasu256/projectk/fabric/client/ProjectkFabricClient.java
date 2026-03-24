package com.karasu256.projectk.fabric.client;

import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;

import java.io.IOException;

public final class ProjectkFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProjectKClient.init();
        ProjectKClient.initLate();

        CoreShaderRegistrationCallback.EVENT.register(ctx -> {
            ProjectKCoreShaders.init((id, format, onLoaded) -> {
                try {
                    ctx.register(id, format, onLoaded);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to register ProjectK core shader: " + id, e);
                }
            });
        });
    }
}
