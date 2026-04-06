package com.karasu256.projectk.particle;

import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record LaserParticleOptions(ResourceLocation energyId, Vec3 target, double scale, int lifetime) implements ParticleOptions {
    public static final MapCodec<LaserParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("energy_id", ProjectKEnergies.ABYSS.id()).forGetter(LaserParticleOptions::energyId),
            Vec3.CODEC.optionalFieldOf("target", Vec3.ZERO).forGetter(LaserParticleOptions::target),
            Codec.DOUBLE.optionalFieldOf("scale", 0.15).forGetter(LaserParticleOptions::scale),
            Codec.INT.optionalFieldOf("lifetime", 20).forGetter(LaserParticleOptions::lifetime)
    ).apply(instance, LaserParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new);
 
    public static final StreamCodec<RegistryFriendlyByteBuf, LaserParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, LaserParticleOptions::energyId,
            VEC3_STREAM_CODEC, LaserParticleOptions::target,
            ByteBufCodecs.DOUBLE, LaserParticleOptions::scale,
            ByteBufCodecs.INT, LaserParticleOptions::lifetime,
            LaserParticleOptions::new);

    @Override
    @NotNull
    public ParticleType<?> getType() {
        return ProjectKParticles.ABYSS_LASER_PARTICLE.get();
    }
}
