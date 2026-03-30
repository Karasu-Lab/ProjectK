package com.karasu256.projectk.client;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.client.render.entity.AbyssEnergyEntityRenderer;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.particle.AbyssParticle;
import com.karasu256.projectk.particle.ProjectKParticles;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.karasuniki.karasunikilib.api.client.registry.IKClientRegistryTarget;
import net.karasuniki.karasunikilib.api.client.registry.KClientRegistry;
import net.karasuniki.karasunikilib.bridge.geckolib.GeckoLibBridge;

@KClientRegistry(modId = ProjectK.MOD_ID)
public class ProjectKClient implements IKClientRegistryTarget {
    public static void init() {
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
    }

    @Override
    public void registerRenderers() {
        GeckoLibBridge.get().registerBlockRenderer(ProjectKBlockEntities.ABYSS_CORE.get());
        GeckoLibBridge.get().registerBlockRenderer(ProjectKBlockEntities.ABYSS_GENERATOR.get());
    }

    public static void initLate() {
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
    }
}
