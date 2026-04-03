package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AbyssEnchanterTier(int level, long cost) {
    public static final Codec<AbyssEnchanterTier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("level").forGetter(AbyssEnchanterTier::level),
            Codec.LONG.fieldOf("cost").forGetter(AbyssEnchanterTier::cost)
    ).apply(instance, AbyssEnchanterTier::new));
}
