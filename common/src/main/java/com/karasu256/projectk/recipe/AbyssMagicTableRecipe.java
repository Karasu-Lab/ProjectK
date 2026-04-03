package com.karasu256.projectk.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record AbyssMagicTableRecipe(ResourceLocation energyId, long energyAmount, IngredientStack input,
                                    ItemStack result) implements Recipe<RecipeInput> {
    public static final MapCodec<AbyssMagicTableRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("energy_id").forGetter(AbyssMagicTableRecipe::energyId),
            Codec.LONG.fieldOf("energy_amount").forGetter(AbyssMagicTableRecipe::energyAmount),
            IngredientStack.CODEC.codec().fieldOf("input").forGetter(AbyssMagicTableRecipe::input),
            ItemStack.CODEC.fieldOf("result").forGetter(AbyssMagicTableRecipe::result)
    ).apply(builder, AbyssMagicTableRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssMagicTableRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            AbyssMagicTableRecipe::energyId,
            ByteBufCodecs.VAR_LONG,
            AbyssMagicTableRecipe::energyAmount,
            IngredientStack.STREAM_CODEC,
            AbyssMagicTableRecipe::input,
            ItemStack.STREAM_CODEC,
            AbyssMagicTableRecipe::result,
            AbyssMagicTableRecipe::new
    );

    public boolean matchesInput(ItemStack stack) {
        return input.ingredient().test(stack) && stack.getCount() >= input.count();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ProjectKRecipes.ABYSS_MAGIC_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ProjectKRecipes.ABYSS_MAGIC_TABLE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<AbyssMagicTableRecipe> {
        @Override
        public MapCodec<AbyssMagicTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AbyssMagicTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
