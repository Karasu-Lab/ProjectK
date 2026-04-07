package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.energy.PKMaterials;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.ProjectKClient;
import com.karasu256.projectk.client.ProjectKCoreShaders;
import com.karasu256.projectk.client.screen.*;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.menu.ProjectKMenus;
import com.karasu256.projectk.neoforge.config.ProjectKNeoForgeConfig;
import com.karasu256.projectk.neoforge.integrations.NeoForgeModIntegrationSupplier;
import com.karasu256.projectk.neoforge.platform.NeoForgeProjectKPlatform;
import com.karasu256.projectk.platform.PlatformServices;
import com.karasu256.projectk.registry.ItemsRegistry;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.ModIntegrationBootstrapper;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;

import java.io.IOException;
import java.io.UncheckedIOException;

@SuppressWarnings("DataFlowIssue")
@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge(ModContainer container) {
        PlatformServices.register(new NeoForgeProjectKPlatform());
        ProjectK.init();
        ProjectKNeoForgeConfig.init(container);
        ModIntegrationBootstrapper.bootstrap(new NeoForgeModIntegrationSupplier<>(
                        "com.karasu256.projectk.neoforge.integrations.projecte.ProjectEIntegration"),
                new NeoForgeModIntegrationSupplier<>(
                        "com.karasu256.projectk.neoforge.integrations.mekanism.MekanismIntegration"),
                new NeoForgeModIntegrationSupplier<>(
                        "com.karasu256.projectk.neoforge.integrations.forge.ForgeEnergyIntegration"));

        if (FMLEnvironment.dist.isClient()) {
            container.getEventBus().addListener(this::onClientSetup);
            container.getEventBus().addListener(this::onRegisterScreens);
            container.getEventBus().addListener(this::onRegisterShaders);
            container.getEventBus().addListener(this::onModelRegisterAdditional);
            container.getEventBus().addListener(this::onRegisterClientExtensions);
        }
    }

    private static void registerItemModelProperties() {
        ProjectKItems.init();
        ResourceLocation propertyId = Id.id("abyss_energy");
        for (ResourceLocation itemId : ItemsRegistry.getEnergySuffixItems()) {
            ItemProperties.register(BuiltInRegistries.ITEM.get(itemId), propertyId,
                    (stack, level, entity, seed) -> ProjectKClient.getAbyssEnergyModelIndex(stack));
        }
    }

    private static void registerFluidExtensions(RegisterClientExtensionsEvent event, int tintColor, FluidType fluidType) {
        ResourceLocation still = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/base_fluid_still");
        ResourceLocation flow = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/base_fluid_flow");
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
                return tintColor;
            }
        }, fluidType);
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

    @SuppressWarnings("deprecation")
    private void onClientSetup(FMLClientSetupEvent event) {
        ProjectKClient.init();
        ProjectKClient.initLate();
        registerItemModelProperties();
        ItemBlockRenderTypes.setRenderLayer(ProjectKBlocks.ABYSS_GENERATOR.get(), RenderType.CUTOUT);
        ItemBlockRenderTypes.setRenderLayer(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKBlocks.FLUID_ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKBlocks.FLUID_YIN_ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKBlocks.FLUID_YANG_ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKFluids.ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKFluids.FLOWING_ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKFluids.YIN_ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKFluids.FLOWING_YIN_ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKFluids.YANG_ABYSS_ENERGY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ProjectKFluids.FLOWING_YANG_ABYSS_ENERGY.get(), RenderType.translucent());
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
        registerFluidExtensions(event, PKMaterials.ABYSS.color() | 0xFF000000,
                ProjectKFluids.ABYSS_ENERGY.get().getFluidType());
        registerFluidExtensions(event, PKMaterials.YIN.color() | 0xFF000000,
                ProjectKFluids.YIN_ABYSS_ENERGY.get().getFluidType());
        registerFluidExtensions(event, PKMaterials.YANG.color() | 0xFF000000,
                ProjectKFluids.YANG_ABYSS_ENERGY.get().getFluidType());
    }
}
