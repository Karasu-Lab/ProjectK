package com.karasu256.projectk.neoforge.particle;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.particle.*;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;

public class NeoForgeProjectKParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ProjectK.MOD_ID,
            Registries.PARTICLE_TYPE);

    public static void init() {
        ProjectKParticles.ABYSS_PARTICLE = PARTICLES.register(AbyssParticleOptions.ID, () -> AbyssParticleOptions.TYPE);
        ProjectKParticles.ABYSS_PORTAL_PARTICLE = PARTICLES.register(AbyssPortalParticleOptions.ID,
                () -> AbyssPortalParticleOptions.TYPE);
        ProjectKParticles.ABYSS_LASER_PARTICLE = PARTICLES.register(AbyssLaserParticleOptions.ID,
                () -> AbyssLaserParticleOptions.TYPE);
        ProjectKParticles.ABYSS_BURST_PARTICLE = PARTICLES.register(AbyssBurstParticleOptions.ID,
                () -> AbyssBurstParticleOptions.TYPE);
        ProjectKParticles.ABYSS_BURST_RESIDUAL_PARTICLE = PARTICLES.register(AbyssBurstResidualParticleOptions.ID,
                () -> AbyssBurstResidualParticleOptions.TYPE);

        PARTICLES.register();
    }
}
