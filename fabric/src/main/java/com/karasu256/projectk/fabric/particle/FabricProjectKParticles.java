package com.karasu256.projectk.fabric.particle;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.particle.*;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;

public class FabricProjectKParticles {
    private static final Registrar<ParticleType<?>> PARTICLES = RegistrarManager.get(ProjectK.MOD_ID)
            .get(Registries.PARTICLE_TYPE);

    public static void init() {
        ProjectKParticles.ABYSS_PARTICLE = register(AbyssParticleOptions.ID, AbyssParticleOptions.TYPE);
        ProjectKParticles.ABYSS_PORTAL_PARTICLE = register(AbyssPortalParticleOptions.ID,
                AbyssPortalParticleOptions.TYPE);
        ProjectKParticles.ABYSS_LASER_PARTICLE = register(AbyssLaserParticleOptions.ID, AbyssLaserParticleOptions.TYPE);
        ProjectKParticles.ABYSS_BURST_PARTICLE = register(AbyssBurstParticleOptions.ID, AbyssBurstParticleOptions.TYPE);
        ProjectKParticles.ABYSS_BURST_RESIDUAL_PARTICLE = register(AbyssBurstResidualParticleOptions.ID,
                AbyssBurstResidualParticleOptions.TYPE);
    }

    private static <T extends ParticleType<?>> RegistrySupplier<T> register(String id, T object) {
        return PARTICLES.register(Id.id(id), () -> object);
    }
}
