package com.karasu256.projectk.fabric.client;

import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import com.karasu256.projectk.client.resource.ProjectKDynamicResources;
import com.karasu256.projectk.client.screen.*;
import com.karasu256.projectk.client.PKRenderProxy;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.menu.ProjectKMenus;
import com.karasu256.projectk.particle.*;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.io.IOException;
import java.io.UncheckedIOException;

@SuppressWarnings("unused")
public class ProjectkFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProjectKDynamicResources.init();
        initializeCommoon();
        registerParticles(ParticleFactoryRegistry.getInstance());
        registerMenus();
        ProjectKItems.init();
        registerRenderLayers();
        registerFluidRendering();
        registerShaders();
    }

    private void initializeCommoon() {
        ProjectKClient.init();
    }

    private void registerMenus() {
        MenuScreens.register(ProjectKMenus.ABYSS_MAGIC_TABLE.get(), AbyssMagicTableScreen::new);
        MenuScreens.register(ProjectKMenus.ABYSS_ALCHEMY_BLEND_MACHINE.get(), AbyssAlchemyBlendMachineScreen::new);
        MenuScreens.register(ProjectKMenus.ABYSS_ENCHANTER.get(), AbyssEnchanterScreen::new);
        MenuScreens.register(ProjectKMenus.ABYSS_CHARGER.get(), AbyssChargerScreen::new);
        MenuScreens.register(ProjectKMenus.ABYSS_STORAGE.get(), AbyssStorageScreen::new);
        MenuScreens.register(ProjectKMenus.ABYSS_ENCHANT_REMOVER.get(), AbyssEnchantRemoverScreen::new);
        MenuScreens.register(ProjectKMenus.ABYSS_SYNTHESIZER.get(), AbyssSynthesizerScreen::new);
    }

    private void registerRenderLayers() {
        ProjectKClient.registerRenderLayers(new PKRenderProxy.PKRenderTypeRegistrar() {
            @Override
            public void register(RegistrySupplier<? extends Block> block, RenderType type) {
                BlockRenderLayerMap.INSTANCE.putBlock(block.get(), type);
            }

            @Override
            public void registerFluid(RegistrySupplier<? extends Fluid> fluid, RenderType type) {
                BlockRenderLayerMap.INSTANCE.putFluids(type, fluid.get());
            }
        });
    }

    private void registerFluidRendering() {
        ProjectKClient.registerFluidRendering((source, flowing, color, still, flow) -> {
            FluidRenderHandlerRegistry.INSTANCE.register(source.get(), flowing.get(),
                    new SimpleFluidRenderHandler(still, flow, color));
        });
    }

    private void registerShaders() {
        CoreShaderRegistrationCallback.EVENT.register(this::registerCoreShader);
    }

    private void registerCoreShader(CoreShaderRegistrationCallback.RegistrationContext context) {
        ProjectKCoreShaders.init((id, format, onLoaded) -> {
            try {
                context.register(id, format, onLoaded);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private static void registerParticles(ParticleFactoryRegistry registry) {
        registry.register(ProjectKParticles.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
        registry.register(ProjectKParticles.ABYSS_PORTAL_PARTICLE.get(),
                spriteSet -> (options, level, x, y, z, vx, vy, vz) -> new AbyssParticle(level, x, y, z, vx, vy, vz,
                        spriteSet, new AbyssParticleOptions(options.energyId())));
        registry.register(ProjectKParticles.ABYSS_LASER_PARTICLE.get(), context -> new AbyssLaserParticle.Provider());
        registry.register(ProjectKParticles.ABYSS_BURST_PARTICLE.get(), spriteSet -> new AbyssBurstParticle.Provider());
        registry.register(ProjectKParticles.ABYSS_BURST_RESIDUAL_PARTICLE.get(),
                AbyssBurstResidualParticle.Provider::new);
    }
}
