package com.karasu256.projectk.particle;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;

@KRegistry(modId = ProjectK.MOD_ID, order = 1)
public class ProjectKParticles implements IKRegistryTarget {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ProjectK.MOD_ID,
            Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<ParticleType<AbyssParticleOptions>> ABYSS_PARTICLE = PARTICLES.register(
            AbyssParticleOptions.ID, () -> AbyssParticleOptions.TYPE);
    public static final RegistrySupplier<ParticleType<AbyssPortalParticleOptions>> ABYSS_PORTAL_PARTICLE = PARTICLES.register(
            AbyssPortalParticleOptions.ID, () -> AbyssPortalParticleOptions.TYPE);
    public static final RegistrySupplier<ParticleType<AbyssLaserParticleOptions>> ABYSS_LASER_PARTICLE = PARTICLES.register(
            AbyssLaserParticleOptions.ID, () -> AbyssLaserParticleOptions.TYPE);
    public static final RegistrySupplier<ParticleType<AbyssBurstParticleOptions>> ABYSS_BURST_PARTICLE = PARTICLES.register(
            AbyssBurstParticleOptions.ID, () -> AbyssBurstParticleOptions.TYPE);
    public static final RegistrySupplier<ParticleType<AbyssBurstResidualParticleOptions>> ABYSS_BURST_RESIDUAL_PARTICLE = PARTICLES.register(
            AbyssBurstResidualParticleOptions.ID, () -> AbyssBurstResidualParticleOptions.TYPE);

    public static void register() {
        PARTICLES.register();
    }
}
