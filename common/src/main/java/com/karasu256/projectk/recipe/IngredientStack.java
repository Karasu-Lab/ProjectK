package com.karasu256.projectk.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientStack(Ingredient ingredient, int count) {
    public static final MapCodec<IngredientStack> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IngredientStack::ingredient),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("count").forGetter(IngredientStack::count)
    ).apply(builder, IngredientStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            IngredientStack::ingredient,
            ByteBufCodecs.VAR_INT,
            IngredientStack::count,
            IngredientStack::new
    );
}
