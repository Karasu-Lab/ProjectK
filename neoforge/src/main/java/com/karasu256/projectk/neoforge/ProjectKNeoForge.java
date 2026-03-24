package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.integration.ModIntegrationBootstrapper;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.neoforge.config.ProjectKNeoForgeConfig;
import com.karasu256.projectk.neoforge.integrations.NeoForgeModIntegrationSupplier;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.io.UncheckedIOException;

@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge(ModContainer container) {
        ProjectK.init();
        ProjectKNeoForgeConfig.init(container);
        ModIntegrationBootstrapper.bootstrap(
            new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.projecte.ProjectEIntegration"),
            new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.mekanism.MekanismIntegration"),
            new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.forge.ForgeEnergyIntegration")
        );

        if (FMLEnvironment.dist.isClient()) {
            ProjectKClient.init();
            container.getEventBus().addListener(this::onClientSetup);
            container.getEventBus().addListener(this::onRegisterShaders);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        ProjectKClient.initLate();
    }

    private void onRegisterShaders(RegisterShadersEvent event) {
        com.karasu256.projectk.client.ProjectKCoreShaders.init((id, format, onLoaded) -> {
            try {
                event.registerShader(new ShaderInstance(event.getResourceProvider(), id, format), onLoaded);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
