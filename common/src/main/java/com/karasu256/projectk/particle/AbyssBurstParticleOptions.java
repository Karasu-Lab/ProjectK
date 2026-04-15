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

public record AbyssBurstParticleOptions(ResourceLocation energyId) implements ParticleOptions {
    public static final MapCodec<AbyssBurstParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf(EnergyKeys.ENERGY_ID.toString(),
                            ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL))
                    .forGetter(AbyssBurstParticleOptions::energyId)).apply(instance, AbyssBurstParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssBurstParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AbyssBurstParticleOptions::energyId, AbyssBurstParticleOptions::new);

    public static final String ID = "abyss_burst";

    @Override
    @NotNull
    public ParticleType<?> getType() {
        return TYPE;
    }

    public static ParticleType<AbyssBurstParticleOptions> TYPE = new ParticleType<>(false) {
        @Override
        @NotNull
        public MapCodec<AbyssBurstParticleOptions> codec() {
            return AbyssBurstParticleOptions.CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<? super RegistryFriendlyByteBuf, AbyssBurstParticleOptions> streamCodec() {
            return AbyssBurstParticleOptions.STREAM_CODEC;
        }
    };
}
