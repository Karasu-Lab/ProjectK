package com.karasu256.projectk.client;

import com.karasu256.projectk.api.energy.PKMaterials;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.client.render.block.AbyssAbsorptionPrismRenderer;
import com.karasu256.projectk.client.render.block.AbyssEnergyCableRenderer;
import com.karasu256.projectk.client.render.block.geckolib.AbyssGeoBlockRenderer;
import com.karasu256.projectk.client.render.entity.AbyssEnergyEntityRenderer;
import com.karasu256.projectk.client.render.entity.AbyssLaserEntityRenderer;
import com.karasu256.projectk.client.util.PKColorUtils;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.particle.AbyssLaserParticle;
import com.karasu256.projectk.particle.AbyssParticle;
import com.karasu256.projectk.particle.ProjectKParticles;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.world.item.ItemStack;

public class ProjectKClient {
    public static void init() {
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_LASER_ENTITY, AbyssLaserEntityRenderer::new);

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if (tintIndex == 0) {
                AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
                if (data != null) {
                    return PKColorUtils.getEnergyColor(data.energyId(), PKColorUtils.OPAQUE);
                }
            }
            return 0xFFFFFFFF;
        }, ProjectKItems.ABYSS_INGOT.get());

        for (PKMaterials material : PKMaterials.values()) {
            ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
                if (tintIndex == 1) {
                    return PKColorUtils.getEnergyColor(Id.id(material.energyIdPath()), PKColorUtils.SEMI_TRANSPARENT);
                }
                return 0xFFFFFFFF;
            }, ProjectKItems.getBucketByMaterial(material).get());

            ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
                return PKColorUtils.getEnergyColor(Id.id(material.energyIdPath()), PKColorUtils.SEMI_TRANSPARENT);
            }, ProjectKBlocks.getFluidBlockByMaterial(material).get());
        }
    }

    public static void initLate() {
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_LASER_PARTICLE.get(),
                context -> new AbyssLaserParticle.Provider());
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_CORE.get(),
                context -> new AbyssGeoBlockRenderer<>());
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_ENERGY_CABLE.get(),
                AbyssEnergyCableRenderer::new);
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_ABSORPTION_PRISM.get(),
                AbyssAbsorptionPrismRenderer::new);
    }

    public static float getAbyssEnergyModelIndex(ItemStack stack) {
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (data == null) {
            return 0.0f;
        }
        return ProjectKEnergies.getModelPredicateValue(data.energyId());
    }
}
