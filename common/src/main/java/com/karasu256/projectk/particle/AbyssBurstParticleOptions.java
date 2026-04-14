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
            instance -> instance.group(
                            ResourceLocation.CODEC.optionalFieldOf(EnergyKeys.ENERGY_ID.toString(), ProjectKEnergies.ABYSS.id())
                                    .forGetter(AbyssBurstParticleOptions::energyId))
                    .apply(instance, AbyssBurstParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssBurstParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AbyssBurstParticleOptions::energyId, AbyssBurstParticleOptions::new);

    @Override
    @NotNull
    public ParticleType<?> getType() {
        return ProjectKParticles.ABYSS_BURST_PARTICLE.get();
    }
}
