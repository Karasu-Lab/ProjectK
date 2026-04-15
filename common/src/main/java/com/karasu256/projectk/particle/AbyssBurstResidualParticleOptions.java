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

public record AbyssBurstResidualParticleOptions(ResourceLocation energyId) implements ParticleOptions {
    public static final MapCodec<AbyssBurstResidualParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf(EnergyKeys.ENERGY_ID.toString(),
                                    ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL))
                            .forGetter(AbyssBurstResidualParticleOptions::energyId))
                    .apply(instance, AbyssBurstResidualParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssBurstResidualParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AbyssBurstResidualParticleOptions::energyId,
            AbyssBurstResidualParticleOptions::new);

    public static final String ID = "abyss_burst_residual";

    @Override
    @NotNull
    public ParticleType<?> getType() {
        return TYPE;
    }

    public static ParticleType<AbyssBurstResidualParticleOptions> TYPE = new ParticleType<>(true) {
        @Override
        @NotNull
        public MapCodec<AbyssBurstResidualParticleOptions> codec() {
            return AbyssBurstResidualParticleOptions.CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<? super RegistryFriendlyByteBuf, AbyssBurstResidualParticleOptions> streamCodec() {
            return AbyssBurstResidualParticleOptions.STREAM_CODEC;
        }
    };

}
