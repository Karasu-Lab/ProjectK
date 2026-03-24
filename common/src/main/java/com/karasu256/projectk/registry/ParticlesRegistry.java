package com.karasu256.projectk.registry;

import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ParticlesRegistry {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> ABYSS_PARTICLE = PARTICLES.register(Id.id("abyss_particle"), () -> new SimpleParticleType(false) {
    });

    public static void register() {
        PARTICLES.register();
    }
}
