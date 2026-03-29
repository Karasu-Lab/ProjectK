package com.karasu256.projectk.registry;

import com.karasu256.projectk.particle.AbyssParticleOptions;
import com.karasu256.projectk.utils.Id;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ParticlesRegistry {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<ParticleType<AbyssParticleOptions>> ABYSS_PARTICLE = PARTICLES.register(Id.id("abyss_particle"), () -> new ParticleType<>(false) {
        @Override
        @NotNull
        public MapCodec<AbyssParticleOptions> codec() {
            return AbyssParticleOptions.CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<? super RegistryFriendlyByteBuf, AbyssParticleOptions> streamCodec() {
            return AbyssParticleOptions.STREAM_CODEC;
        }
    });

    public static void register() {
        PARTICLES.register();
    }
}
