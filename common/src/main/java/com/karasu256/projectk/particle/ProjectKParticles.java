package com.karasu256.projectk.particle;

import com.karasu256.projectk.ProjectK;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KParticleRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 1)
public class ProjectKParticles implements IKRegistryInitializerTarget {
    public static final RegistrySupplier<ParticleType<AbyssParticleOptions>> ABYSS_PARTICLE = KParticleRegistry.particle(ProjectK.MOD_ID, "abyss_particle", () -> new ParticleType<>(false) {
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

    public static void init() {
    }
}
