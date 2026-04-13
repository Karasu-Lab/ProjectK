package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.recipe.AbyssSynthesizerRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AbyssSynthesizerRecipeProvider extends RecipeProvider {
    private RecipeOutput output;

    public AbyssSynthesizerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        this.output = output;
        addSynthesizerRecipe(new AbyssSynthesizerRecipe(List.of(new AbyssEnergyData(ProjectKEnergies.YIN.id(), 1000L),
                new AbyssEnergyData(ProjectKEnergies.YANG.id(), 2000L),
                new AbyssEnergyData(ProjectKEnergies.ABYSS.id(), 500L)),
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

    @Override
    public String getName() {
        return "ProjectK Abyss Synthesizer Recipes";
    }

    private void addSynthesizerRecipe(AbyssSynthesizerRecipe recipe) {
        String resultPath = BuiltInRegistries.ITEM.getKey(recipe.result().getItem()).getPath();
        output.accept(Id.id(resultPath + "_from_abyss_synthesizer"), recipe, null);
    }
}
