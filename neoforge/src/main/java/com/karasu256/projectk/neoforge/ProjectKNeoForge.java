package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import com.karasu256.projectk.neoforge.config.ProjectKNeoForgeConfig;
import com.karasu256.projectk.neoforge.integrations.NeoForgeModIntegrationSupplier;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.ModIntegrationBootstrapper;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.io.UncheckedIOException;

@SuppressWarnings("DataFlowIssue")
@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge(ModContainer container) {
        ProjectK.init();
        ProjectKNeoForgeConfig.init(container);
        ModIntegrationBootstrapper.bootstrap(new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.projecte.ProjectEIntegration"), new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.mekanism.MekanismIntegration"), new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.forge.ForgeEnergyIntegration"));

        if (FMLEnvironment.dist.isClient()) {
            ProjectKClient.init();
            container.getEventBus().addListener(this::onClientSetup);
            container.getEventBus().addListener(this::onRegisterShaders);
            container.getEventBus().addListener(this::onModelRegisterAdditional);
        }
    }

    private void onModelRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (var energy : RegistrarManager.get(KarasunikiLib.MOD_ID).get(KarasunikiRegistries.ENERGY_REGISTRY_KEY)) {
            var energyId = energy.getId();
            event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(), "block/abyss_generator/" + energyId.getPath()), "standalone"));
        }
    }

    @SuppressWarnings("deprecation")
    private void onClientSetup(FMLClientSetupEvent event) {
        ProjectKClient.initLate();
        ItemBlockRenderTypes.setRenderLayer(ProjectKBlocks.ABYSS_GENERATOR.get(), RenderType.CUTOUT);
        ItemBlockRenderTypes.setRenderLayer(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), RenderType.translucent());
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
}
