package com.karasu256.projectk.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public record BiomeCondition(ResourceLocation id, boolean tag) {
    public static final MapCodec<BiomeCondition> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(BiomeCondition::id),
            Codec.BOOL.fieldOf("tag").forGetter(BiomeCondition::tag)
    ).apply(builder, BiomeCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BiomeCondition> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            BiomeCondition::id,
            ByteBufCodecs.BOOL,
            BiomeCondition::tag,
            BiomeCondition::new
    );

    public boolean matches(Level level, BlockPos pos) {
        if (tag) {
            TagKey<Biome> tagKey = TagKey.create(Registries.BIOME, id);
            return level.getBiome(pos).is(tagKey);
        }
        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, id);
        return level.getBiome(pos).is(key);
    }
}
