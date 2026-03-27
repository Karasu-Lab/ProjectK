package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import com.karasu256.projectk.neoforge.client.NeoForgeGeckoLibHelper;
import com.karasu256.projectk.neoforge.config.ProjectKNeoForgeConfig;
import com.karasu256.projectk.neoforge.integrations.NeoForgeModIntegrationSupplier;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import net.karasuniki.karasunikilib.api.ModIntegrationBootstrapper;
import net.karasuniki.karasunikilib.bridge.geckolib.GeckoLibBridge;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.io.UncheckedIOException;

@SuppressWarnings("DataFlowIssue")
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
            container.getEventBus().addListener(this::onRegisterRenderers);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        ProjectKClient.initLate();
    }

    private void onRegisterShaders(RegisterShadersEvent event) {
        ProjectKCoreShaders.init((id, format, onLoaded) -> {
            try {
                event.registerShader(new ShaderInstance(event.getResourceProvider(), id, format), onLoaded);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        NeoForgeGeckoLibHelper.setEvent(event);
        GeckoLibBridge.get().registerBlockRenderer(BlockEntitiesRegistry.ABYSS_CORE.get());
        ProjectKClient.registerRenderers();
        NeoForgeGeckoLibHelper.setEvent(null);
    }
}
