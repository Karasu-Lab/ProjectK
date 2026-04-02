package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.recipe.AbyssMagicTableRecipe;
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

import java.util.concurrent.CompletableFuture;

public class AbyssMagicTableRecipeProvider extends RecipeProvider {
    private RecipeOutput output;

    public AbyssMagicTableRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        this.output = output;

        ItemStack abyssIngot = new ItemStack(ProjectKItems.ABYSS_INGOT.get());
        AbyssEnergyData.applyToStack(abyssIngot, ProjectKEnergies.ABYSS_ENERGY.get().getId(), 500L);
        addAbyssMagicTableRecipe(new AbyssMagicTableRecipe(ProjectKEnergies.ABYSS_ENERGY.get().getId(), 500L, new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1), abyssIngot));

        ItemStack yinIngot = new ItemStack(ProjectKItems.YIN_ABYSS_INGOT.get());
        AbyssEnergyData.applyToStack(yinIngot, ProjectKEnergies.YIN_ABYSS_ENERGY.get().getId(), 500L);
        addAbyssMagicTableRecipe(new AbyssMagicTableRecipe(ProjectKEnergies.YIN_ABYSS_ENERGY.get().getId(), 500L, new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1), yinIngot));

        ItemStack yangIngot = new ItemStack(ProjectKItems.YANG_ABYSS_INGOT.get());
        AbyssEnergyData.applyToStack(yangIngot, ProjectKEnergies.YANG_ABYSS_ENERGY.get().getId(), 500L);
        addAbyssMagicTableRecipe(new AbyssMagicTableRecipe(ProjectKEnergies.YANG_ABYSS_ENERGY.get().getId(), 500L, new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1), yangIngot));
    }

    @Override
    public String getName() {
        return "ProjectK Abyss Magic Table Recipes";
    }

    private void addAbyssMagicTableRecipe(AbyssMagicTableRecipe recipe) {
        String resultPath = BuiltInRegistries.ITEM.getKey(recipe.result().getItem()).getPath();
        output.accept(Id.id(resultPath + "_from_magic_table"), recipe, null);
    }
}
