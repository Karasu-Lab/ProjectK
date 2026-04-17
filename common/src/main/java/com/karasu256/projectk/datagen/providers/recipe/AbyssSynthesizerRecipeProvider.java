package com.karasu256.projectk.datagen.providers.recipe;

import com.karasu256.projectk.api.datagen.impl.AbstractAbyssSynthesizerRecipeProvider;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.recipe.AbyssSynthesizerRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AbyssSynthesizerRecipeProvider extends AbstractAbyssSynthesizerRecipeProvider {
    public AbyssSynthesizerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "abyss_synthesizer", registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        super.buildRecipes(output);
        addSynthesizerRecipe(new AbyssSynthesizerRecipe(
                List.of(new AbyssEnergyData(ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.YIN), 1000L),
                        new AbyssEnergyData(ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.YANG),
                                2000L),
                        new AbyssEnergyData(ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL),
                                500L)),
                List.of(new IngredientStack(Ingredient.of(ProjectKItems.ABYSS_INGOT.get()), 1),
                        new IngredientStack(Ingredient.of(ProjectKItems.ABYSS_INGOT.get()), 1),
                        new IngredientStack(Ingredient.of(ProjectKItems.ABYSS_INGOT.get()), 1),
                        new IngredientStack(Ingredient.of(Items.GOLD_INGOT), 1),
                        new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1)), new ItemStack(Items.DIAMOND)));

        addSynthesizerRecipe(new AbyssSynthesizerRecipe(List.of(),
                List.of(new IngredientStack(Ingredient.of(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get()), 1, List.of(), 1,
                                1000L),
                        new IngredientStack(Ingredient.of(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get()), 1, List.of(), 1,
                                1000L),
                        new IngredientStack(Ingredient.of(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get()), 1, List.of(), 1,
                                1000L),
                        new IngredientStack(Ingredient.of(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get()), 1, List.of(), 1,
                                1000L),
                        new IngredientStack(Ingredient.of(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get()), 1, List.of(), 1,
                                1000L),
                        new IngredientStack(Ingredient.of(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get()), 1, List.of(), 1,
                                1000L)), new ItemStack(ProjectKBlocks.ABYSS_PORTAL.get()), 0, 0, 6000L));
    }
}
