package com.karasu256.projectk.recipe;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record AbyssSynthesizerRecipe(List<AbyssEnergyData> energies, List<IngredientStack> inputs,
                                     ItemStack result) implements Recipe<RecipeInput> {
    public static final MapCodec<AbyssSynthesizerRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            AbyssEnergyData.CODEC.listOf().fieldOf("energies").forGetter(AbyssSynthesizerRecipe::energies),
            IngredientStack.CODEC.codec().listOf().fieldOf("inputs").forGetter(AbyssSynthesizerRecipe::inputs),
            ItemStack.CODEC.fieldOf("result").forGetter(AbyssSynthesizerRecipe::result)
    ).apply(builder, AbyssSynthesizerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbyssSynthesizerRecipe> STREAM_CODEC = StreamCodec.composite(
            AbyssEnergyData.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AbyssSynthesizerRecipe::energies,
            IngredientStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AbyssSynthesizerRecipe::inputs,
            ItemStack.STREAM_CODEC,
            AbyssSynthesizerRecipe::result,
            AbyssSynthesizerRecipe::new
    );

    public boolean matchesContainer(Container container) {
        List<ItemStack> providedItems = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                providedItems.add(stack.copy());
            }
        }

        for (IngredientStack req : inputs) {
            boolean matched = false;
            for (int i = 0; i < providedItems.size(); i++) {
                ItemStack provided = providedItems.get(i);
                if (req.ingredient().test(provided) && provided.getCount() >= req.count()) {
                    provided.shrink(req.count());
                    if (provided.isEmpty()) {
                        providedItems.remove(i);
                    }
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        return true;
    }

    public boolean checkEnergies(List<AbyssEnergyData> availableEnergies) {
        for (AbyssEnergyData req : energies) {
            boolean found = false;
            for (AbyssEnergyData available : availableEnergies) {
                if (available.energyId().equals(req.energyId()) && available.amount() >= req.amount()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean matches(@NotNull RecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput input, HolderLookup.@NotNull Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return result.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ProjectKRecipes.ABYSS_SYNTHESIZER_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ProjectKRecipes.ABYSS_SYNTHESIZER.get();
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    public static class Serializer implements RecipeSerializer<AbyssSynthesizerRecipe> {
        @Override
        public @NotNull MapCodec<AbyssSynthesizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, AbyssSynthesizerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
