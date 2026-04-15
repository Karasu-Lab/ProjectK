package com.karasu256.projectk.particle;

import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AbyssParticleOptions(ResourceLocation energyId) implements ParticleOptions {
    public static final MapCodec<AbyssParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf(EnergyKeys.ENERGY_ID.toString(),
                            ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL))
                    .forGetter(AbyssParticleOptions::energyId)).apply(instance, AbyssParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AbyssParticleOptions::energyId, AbyssParticleOptions::new);

    public static final String ID = "abyss";

    @Override
    @NotNull
    public ParticleType<?> getType() {
        return TYPE;
    }

    public static ParticleType<AbyssParticleOptions> TYPE = new ParticleType<>(false) {
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
    };
}
