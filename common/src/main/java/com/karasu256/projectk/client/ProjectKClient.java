package com.karasu256.projectk.client;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.client.render.block.AbyssAbsorptionPrismRenderer;
import com.karasu256.projectk.client.render.block.AbyssEnergyCableRenderer;
import com.karasu256.projectk.client.render.block.geckolib.AbyssGeoBlockRenderer;
import com.karasu256.projectk.client.render.entity.AbyssEnergyEntityRenderer;
import com.karasu256.projectk.client.render.entity.AbyssLaserEntityRenderer;
import com.karasu256.projectk.client.util.PKColorUtils;
import com.karasu256.projectk.client.util.PKRenderProxy;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.particle.*;
import com.karasu256.projectk.registry.ItemsRegistry;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ProjectKClient {
    public static void init() {
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_PORTAL_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_LASER_ENTITY, AbyssLaserEntityRenderer::new);
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_BURST_ENTITY, NoopRenderer::new);

        ResourceLocation propertyId = Id.id("abyss_energy");
        for (ResourceLocation itemId : ItemsRegistry.getEnergySuffixItems()) {
            ItemPropertiesRegistry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemId),
                    propertyId, (stack, level, entity, seed) -> getAbyssEnergyModelIndex(stack));
        }

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if (tintIndex == 0) {
                AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
                if (data != null) {
                    return PKColorUtils.getEnergyColor(data.energyId(), PKColorUtils.OPAQUE);
                }
            }
            return 0xFFFFFFFF;
        }, ProjectKItems.ABYSS_INGOT.get(), ProjectKBlocks.ABYSS_CORE.get(), ProjectKBlocks.ABYSS_PORTAL.get());

        ItemPropertiesRegistry.register(ProjectKBlocks.ABYSS_CORE.get().asItem(), propertyId,
                (stack, level, entity, seed) -> getAbyssEnergyModelIndex(stack));
        ItemPropertiesRegistry.register(ProjectKBlocks.ABYSS_PORTAL.get().asItem(), propertyId,
                (stack, level, entity, seed) -> getAbyssEnergyModelIndex(stack));

        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
                if (tintIndex == 1) {
                    return PKColorUtils.getEnergyColor(definition.id(), PKColorUtils.SEMI_TRANSPARENT);
                }
                return 0xFFFFFFFF;
            }, ProjectKItems.getBucket(definition.id()).get());

            ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                return PKColorUtils.getEnergyColor(definition.id(), PKColorUtils.SEMI_TRANSPARENT);
            }, ProjectKBlocks.getFluidBlock(definition.id()).get());
        }
    }

    public static void initLate() {
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_PORTAL_PARTICLE.get(),
                spriteSet -> (options, level, x, y, z, vx, vy, vz) -> new AbyssParticle(level, x, y, z, vx, vy, vz,
                        spriteSet, new AbyssParticleOptions(options.energyId())));
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_LASER_PARTICLE.get(),
                context -> new AbyssLaserParticle.Provider());
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_BURST_PARTICLE.get(),
                AbyssBurstParticle.Provider::new);
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_CORE.get(),
                context -> new AbyssGeoBlockRenderer<>());
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_ENERGY_CABLE.get(),
                AbyssEnergyCableRenderer::new);
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_ABSORPTION_PRISM.get(),
                AbyssAbsorptionPrismRenderer::new);
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
            registrar.register(ProjectKFluids.getSource(definition.id()),
                    ProjectKFluids.getFlowing(definition.id()),
                    PKColorUtils.getEnergyColor(definition.id(), PKColorUtils.OPAQUE));
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
