package com.karasu256.projectk.client;

import com.karasu256.projectk.client.render.block.AbyssGeneratorRenderer;
import com.karasu256.projectk.client.render.entity.AbyssEnergyEntityRenderer;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.particle.AbyssParticle;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.particle.ProjectKParticles;
import com.karasu256.projectk.registry.EntitiesRegistry;
import com.karasu256.projectk.registry.ParticlesRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class ProjectKClient {
    public static void init() {
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
    }

    public static void registerRenderers() {
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_GENERATOR.get(), AbyssGeneratorRenderer::new);
    }

    public static void initLate() {
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_PARTICLE.get(), (sprites) -> new AbyssParticle.Provider(sprites));
    }
}
