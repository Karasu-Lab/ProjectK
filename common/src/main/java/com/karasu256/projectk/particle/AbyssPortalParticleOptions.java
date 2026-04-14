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

public record AbyssPortalParticleOptions(ResourceLocation energyId) implements ParticleOptions {
    public static final MapCodec<AbyssPortalParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            ResourceLocation.CODEC.optionalFieldOf(EnergyKeys.ENERGY_ID.toString(),
                                            ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL))
                                    .forGetter(AbyssPortalParticleOptions::energyId))
                    .apply(instance, AbyssPortalParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssPortalParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AbyssPortalParticleOptions::energyId, AbyssPortalParticleOptions::new);

    @Override
    @NotNull
    public ParticleType<?> getType() {
        return ProjectKParticles.ABYSS_PORTAL_PARTICLE.get();
    }
}
