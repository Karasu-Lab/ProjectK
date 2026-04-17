package com.karasu256.projectk.client;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.client.render.block.AbyssAbsorptionPrismRenderer;
import com.karasu256.projectk.client.render.block.geckolib.AbyssGeoBlockRenderer;
import com.karasu256.projectk.client.render.entity.AbyssBurstEntityRenderer;
import com.karasu256.projectk.client.render.entity.AbyssEnergyEntityRenderer;
import com.karasu256.projectk.client.render.entity.AbyssLaserEntityRenderer;
import com.karasu256.projectk.client.render.model.AbyssEnergyCableModel;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.registry.ItemsRegistry;
import com.karasu256.projectk.utils.Id;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ProjectKClient {
    public static void init() {
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_PORTAL_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_LASER_ENTITY, AbyssLaserEntityRenderer::new);
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_BURST_ENTITY, AbyssBurstEntityRenderer::new);

        ResourceLocation propertyId = Id.id("abyss_energy");
        for (ResourceLocation itemId : ItemsRegistry.getEnergySuffixItems()) {
            ItemPropertiesRegistry.register(BuiltInRegistries.ITEM.get(itemId),
                    propertyId, (stack, level, entity, seed) -> getAbyssEnergyModelIndex(stack));
        }

        ItemPropertiesRegistry.register(ProjectKBlocks.ABYSS_CORE.get().asItem(), propertyId,
                (stack, level, entity, seed) -> getAbyssEnergyModelIndex(stack));
        ItemPropertiesRegistry.register(ProjectKBlocks.ABYSS_PORTAL.get().asItem(), propertyId,
                (stack, level, entity, seed) -> getAbyssEnergyModelIndex(stack));


        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_CORE.get(),
                context -> new AbyssGeoBlockRenderer<>());
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_ABSORPTION_PRISM.get(),
                AbyssAbsorptionPrismRenderer::new);

        ClientHelper.addModelLoaderRegistration(event -> {
            event.register(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "abyss_energy_cable_loader"),
                    new AbyssEnergyCableModel.Loader());
        });
    }

    public static void registerRenderLayers(PKRenderProxy.PKRenderTypeRegistrar registrar) {
        registrar.register(ProjectKBlocks.ABYSS_GENERATOR, RenderType.cutout());
        registrar.register(ProjectKBlocks.ABYSS_ENERGY_CABLE, RenderType.translucent());

        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            registrar.register(ProjectKBlocks.getFluidBlock(definition.id()), RenderType.translucent());
            registrar.registerFluid(ProjectKFluids.getSource(definition.id()), RenderType.translucent());
            registrar.registerFluid(ProjectKFluids.getFlowing(definition.id()),
                    RenderType.translucent());
        }
    }

    public static void registerFluidRendering(PKRenderProxy.PKFluidRenderingRegistrar registrar) {
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            ResourceLocation energyId = definition.id();
            ArchitecturyFluidAttributes attributes = ProjectKFluids.getAttributes(energyId);
            registrar.register(ProjectKFluids.getSource(energyId),
                    ProjectKFluids.getFlowing(energyId),
                    0xFFFFFFFF,
                    attributes.getSourceTexture(),
                    attributes.getFlowingTexture());
        }
    }

    public static float getAbyssEnergyModelIndex(ItemStack stack) {
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (data == null) {
            return 0.0f;
        }
        return ProjectKEnergies.getModelPredicateValue(data.energyId());
    }
}
