package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AbyssLaserEmitterTier(int level, long capacity, long concentration, int lifetime, int pulseInterval) {
    public static final Codec<AbyssLaserEmitterTier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("level").forGetter(AbyssLaserEmitterTier::level),
            Codec.LONG.fieldOf("capacity").forGetter(AbyssLaserEmitterTier::capacity),
            Codec.LONG.fieldOf("concentration").forGetter(AbyssLaserEmitterTier::concentration),
            Codec.INT.fieldOf("lifetime").forGetter(AbyssLaserEmitterTier::lifetime),
            Codec.INT.fieldOf("pulse_interval").forGetter(AbyssLaserEmitterTier::pulseInterval)
    ).apply(instance, AbyssLaserEmitterTier::new));
}
