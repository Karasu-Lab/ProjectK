package com.karasu256.projectk.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record InBiomeInBlockCraftingRecipe(BiomeCondition biome, ResourceLocation blockTag, List<IngredientStack> inputs, float radius, ItemStack result) implements Recipe<RecipeInput> {
    public static final MapCodec<InBiomeInBlockCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            BiomeCondition.CODEC.fieldOf("biome").forGetter(InBiomeInBlockCraftingRecipe::biome),
            ResourceLocation.CODEC.fieldOf("block_tag").forGetter(InBiomeInBlockCraftingRecipe::blockTag),
            IngredientStack.CODEC.codec().listOf().fieldOf("inputs").forGetter(InBiomeInBlockCraftingRecipe::inputs),
            Codec.floatRange(0.0f, 64.0f).optionalFieldOf("radius", 1.0f).forGetter(InBiomeInBlockCraftingRecipe::radius),
            ItemStack.CODEC.fieldOf("result").forGetter(InBiomeInBlockCraftingRecipe::result)
    ).apply(builder, InBiomeInBlockCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InBiomeInBlockCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
            BiomeCondition.STREAM_CODEC,
            InBiomeInBlockCraftingRecipe::biome,
            ResourceLocation.STREAM_CODEC,
            InBiomeInBlockCraftingRecipe::blockTag,
            ByteBufCodecs.collection(java.util.ArrayList::new, IngredientStack.STREAM_CODEC),
            InBiomeInBlockCraftingRecipe::inputs,
            ByteBufCodecs.FLOAT,
            InBiomeInBlockCraftingRecipe::radius,
            ItemStack.STREAM_CODEC,
            InBiomeInBlockCraftingRecipe::result,
            InBiomeInBlockCraftingRecipe::new
    );

    public boolean matchesContext(Level level, BlockState state, net.minecraft.core.BlockPos pos) {
        TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, blockTag);
        if (!state.is(tagKey)) return false;
        if (!state.getCollisionShape(level, pos).isEmpty()) return false;
        return biome.matches(level, pos);
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
        return ProjectKRecipes.IN_BIOME_IN_BLOCK_CRAFTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ProjectKRecipes.IN_BIOME_IN_BLOCK_CRAFTING.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<InBiomeInBlockCraftingRecipe> {
        @Override
        public MapCodec<InBiomeInBlockCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InBiomeInBlockCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
