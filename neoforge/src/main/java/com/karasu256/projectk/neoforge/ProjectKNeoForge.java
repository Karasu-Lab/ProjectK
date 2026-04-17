package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.client.PKRenderProxy;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import com.karasu256.projectk.client.resource.ProjectKDynamicResources;
import com.karasu256.projectk.client.screen.*;
import com.karasu256.projectk.menu.ProjectKMenus;
import com.karasu256.projectk.neoforge.config.ProjectKNeoForgeConfig;
import com.karasu256.projectk.neoforge.integrations.NeoForgeModIntegrationSupplier;
import com.karasu256.projectk.neoforge.platform.NeoForgeProjectKPlatform;
import com.karasu256.projectk.particle.*;
import com.karasu256.projectk.platform.PlatformServices;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.ModIntegrationBootstrapper;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.io.IOException;
import java.io.UncheckedIOException;

@SuppressWarnings({"DataFlowIssue", "unused"})
@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge(ModContainer container) {
        initializeServices();
        initializeCommon();
        initializeConfig(container);
        initializeBootStrap();
        initializeClient(container);
        if (FMLEnvironment.dist.isClient()) {
            ProjectKDynamicResources.init();
        }
    }

    private void initializeServices() {
        PlatformServices.register(new NeoForgeProjectKPlatform());
    }

    private void initializeCommon() {
        ProjectK.init();
    }

    private void initializeConfig(ModContainer container) {
        ProjectKNeoForgeConfig.init(container);
    }

    private void initializeBootStrap() {
        ModIntegrationBootstrapper.bootstrap(new NeoForgeModIntegrationSupplier<>(
                        "com.karasu256.projectk.neoforge.integrations.projecte.ProjectEIntegration"),
                new NeoForgeModIntegrationSupplier<>(
                        "com.karasu256.projectk.neoforge.integrations.mekanism.MekanismIntegration"),
                new NeoForgeModIntegrationSupplier<>(
                        "com.karasu256.projectk.neoforge.integrations.forge.ForgeEnergyIntegration"));
    }

    private void initializeClient(ModContainer container) {
        if (FMLEnvironment.dist.isClient()) {
            container.getEventBus().addListener(this::onClientSetup);
            container.getEventBus().addListener(this::onRegisterScreens);
            container.getEventBus().addListener(this::onRegisterShaders);
            container.getEventBus().addListener(this::onModelRegisterAdditional);
            container.getEventBus().addListener(this::onRegisterClientExtensions);
            container.getEventBus().addListener(this::onRegisterParticleProviders);
        }
    }

    private void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ProjectKMenus.ABYSS_MAGIC_TABLE.get(), AbyssMagicTableScreen::new);
        event.register(ProjectKMenus.ABYSS_ALCHEMY_BLEND_MACHINE.get(), AbyssAlchemyBlendMachineScreen::new);
        event.register(ProjectKMenus.ABYSS_ENCHANTER.get(), AbyssEnchanterScreen::new);
        event.register(ProjectKMenus.ABYSS_CHARGER.get(), AbyssChargerScreen::new);
        event.register(ProjectKMenus.ABYSS_STORAGE.get(), AbyssStorageScreen::new);
        event.register(ProjectKMenus.ABYSS_ENCHANT_REMOVER.get(), AbyssEnchantRemoverScreen::new);
        event.register(ProjectKMenus.ABYSS_SYNTHESIZER.get(), AbyssSynthesizerScreen::new);
    }

    private void onModelRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (var energy : RegistrarManager.get(KarasunikiLib.MOD_ID).get(KarasunikiRegistries.ENERGY_REGISTRY_KEY)) {
            var energyId = energy.getId();
            event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(),
                    "block/abyss_generator/" + energyId.getPath()), "standalone"));
        }
    }

    @SuppressWarnings({"deprecation", "Convert2Lambda"})
    private void onClientSetup(FMLClientSetupEvent event) {
        ProjectKClient.init();
        ProjectKClient.registerRenderLayers(new PKRenderProxy.PKRenderTypeRegistrar() {
            @Override
            public void register(RegistrySupplier<? extends Block> block, RenderType type) {
                ItemBlockRenderTypes.setRenderLayer(block.get(), type);
            }
        });
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

    @SubscribeEvent
    public void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        ProjectKClient.registerFluidRendering((source, flowing, color, still, flow) -> {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return still;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return flow;
                }

                @Override
                public int getTintColor() {
                    return color;
                }
            }, source.get().getFluidType());
        });
    }

    @SubscribeEvent
    public void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ProjectKParticles.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
        event.registerSpriteSet(ProjectKParticles.ABYSS_PORTAL_PARTICLE.get(),
                spriteSet -> (options, level, x, y, z, vx, vy, vz) -> new AbyssParticle(level, x, y, z, vx, vy, vz,
                        spriteSet, new AbyssParticleOptions(options.energyId())));
        event.registerSpecial(ProjectKParticles.ABYSS_LASER_PARTICLE.get(), new AbyssLaserParticle.Provider());
        event.registerSpecial(ProjectKParticles.ABYSS_BURST_PARTICLE.get(), new AbyssBurstParticle.Provider());
        event.registerSpriteSet(ProjectKParticles.ABYSS_BURST_RESIDUAL_PARTICLE.get(),
                AbyssBurstResidualParticle.Provider::new);
    }
}
