package com.karasu256.projectk.particle;

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

public record AbyssLaserParticleOptions(ResourceLocation energyId, Vec3 target, double scale,
                                        int lifetime) implements ParticleOptions {
    public static final MapCodec<AbyssLaserParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("energy_id",
                                            ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL))
                                    .forGetter(AbyssLaserParticleOptions::energyId),
                            Vec3.CODEC.optionalFieldOf("target", Vec3.ZERO).forGetter(AbyssLaserParticleOptions::target),
                            Codec.DOUBLE.optionalFieldOf("scale", 0.15).forGetter(AbyssLaserParticleOptions::scale),
                            Codec.INT.optionalFieldOf("lifetime", 20).forGetter(AbyssLaserParticleOptions::lifetime))
                    .apply(instance, AbyssLaserParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x, ByteBufCodecs.DOUBLE, Vec3::y, ByteBufCodecs.DOUBLE, Vec3::z, Vec3::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssLaserParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AbyssLaserParticleOptions::energyId, VEC3_STREAM_CODEC,
            AbyssLaserParticleOptions::target, ByteBufCodecs.DOUBLE, AbyssLaserParticleOptions::scale,
            ByteBufCodecs.INT, AbyssLaserParticleOptions::lifetime, AbyssLaserParticleOptions::new);

    public static final String ID = "abyss_laser";

    @Override
    @NotNull
    public ParticleType<?> getType() {
        return TYPE;
    }

    public static ParticleType<AbyssLaserParticleOptions> TYPE = new ParticleType<>(false) {
        @Override
        @NotNull
        public MapCodec<AbyssLaserParticleOptions> codec() {
            return AbyssLaserParticleOptions.CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<? super RegistryFriendlyByteBuf, AbyssLaserParticleOptions> streamCodec() {
            return AbyssLaserParticleOptions.STREAM_CODEC;
        }
    };
}
