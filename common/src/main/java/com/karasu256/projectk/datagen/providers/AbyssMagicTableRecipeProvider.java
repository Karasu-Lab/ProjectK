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
import net.minecraft.resources.ResourceLocation;
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
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            ItemStack ingot = new ItemStack(ProjectKItems.ABYSS_INGOT.get());
            AbyssEnergyData.applyToStack(ingot, definition.id(), definition.defaultAmount());
            addAbyssMagicTableRecipe(definition.id(), new AbyssMagicTableRecipe(definition.id(), definition.defaultAmount(), new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1), ingot));
        }
    }

    @Override
    public String getName() {
        return "ProjectK Abyss Magic Table Recipes";
    }

    private void addAbyssMagicTableRecipe(ResourceLocation energyId, AbyssMagicTableRecipe recipe) {
        String resultPath = BuiltInRegistries.ITEM.getKey(recipe.result().getItem()).getPath();
        String energyPath = energyId.getPath();
        output.accept(Id.id(resultPath + "_" + energyPath + "_from_magic_table"), recipe, null);
    }
}
