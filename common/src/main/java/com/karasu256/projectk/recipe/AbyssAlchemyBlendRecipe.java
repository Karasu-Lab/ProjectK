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

public record AbyssAlchemyBlendRecipe(ResourceLocation energyId1, long energyAmount1,
                                     ResourceLocation energyId2, long energyAmount2,
                                     IngredientStack input, ItemStack result) implements Recipe<RecipeInput> {
    public static final MapCodec<AbyssAlchemyBlendRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("energy_id_1").forGetter(AbyssAlchemyBlendRecipe::energyId1),
            Codec.LONG.fieldOf("energy_amount_1").forGetter(AbyssAlchemyBlendRecipe::energyAmount1),
            ResourceLocation.CODEC.fieldOf("energy_id_2").forGetter(AbyssAlchemyBlendRecipe::energyId2),
            Codec.LONG.fieldOf("energy_amount_2").forGetter(AbyssAlchemyBlendRecipe::energyAmount2),
            IngredientStack.CODEC.codec().fieldOf("input").forGetter(AbyssAlchemyBlendRecipe::input),
            ItemStack.CODEC.fieldOf("result").forGetter(AbyssAlchemyBlendRecipe::result)
    ).apply(builder, AbyssAlchemyBlendRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssAlchemyBlendRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            AbyssAlchemyBlendRecipe::energyId1,
            ByteBufCodecs.VAR_LONG,
            AbyssAlchemyBlendRecipe::energyAmount1,
            ResourceLocation.STREAM_CODEC,
            AbyssAlchemyBlendRecipe::energyId2,
            ByteBufCodecs.VAR_LONG,
            AbyssAlchemyBlendRecipe::energyAmount2,
            IngredientStack.STREAM_CODEC,
            AbyssAlchemyBlendRecipe::input,
            ItemStack.STREAM_CODEC,
            AbyssAlchemyBlendRecipe::result,
            AbyssAlchemyBlendRecipe::new
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
        return ProjectKRecipes.ABYSS_ALCHEMY_BLEND_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ProjectKRecipes.ABYSS_ALCHEMY_BLEND.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<AbyssAlchemyBlendRecipe> {
        @Override
        public MapCodec<AbyssAlchemyBlendRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AbyssAlchemyBlendRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
