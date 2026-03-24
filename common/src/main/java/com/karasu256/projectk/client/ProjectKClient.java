package com.karasu256.projectk.client;

import com.karasu256.projectk.client.render.block.AbyssGeneratorRenderer;
import com.karasu256.projectk.client.render.entity.AbyssEnergyEntityRenderer;
import com.karasu256.projectk.particle.AbyssParticle;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import com.karasu256.projectk.registry.EntitiesRegistry;
import com.karasu256.projectk.registry.ParticlesRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class ProjectKClient {
    public static void init() {
    }

    public static void initLate() {
        EntityRendererRegistry.register(EntitiesRegistry.ABYSS_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntitiesRegistry.ABYSS_GENERATOR.get(), AbyssGeneratorRenderer::new);
        ParticleProviderRegistry.register(ParticlesRegistry.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
    }
}
