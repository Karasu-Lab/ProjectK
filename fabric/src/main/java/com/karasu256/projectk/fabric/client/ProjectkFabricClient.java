package com.karasu256.projectk.fabric.client;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import dev.architectury.registry.registries.RegistrarManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.karasuniki.karasunikilib.bridge.geckolib.GeckoLibBridge;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public final class ProjectkFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProjectKClient.init();
        ProjectKClient.registerRenderers();
        ProjectKClient.initLate();
        GeckoLibBridge.get().registerBlockRenderer(ProjectKBlockEntities.ABYSS_CORE.get());

        ModelLoadingPlugin.register(pluginContext -> {
            for (var energy : RegistrarManager.get(ProjectK.MOD_ID).get(KarasunikiRegistries.ENERGY_REGISTRY_KEY)) {
                var energyId = energy.getId();
                pluginContext.addModels(ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(), "block/abyss_generator/" + energyId.getPath()));
            }
        });

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
