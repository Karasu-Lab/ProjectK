package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.api.datagen.impl.AbstractAbyssMagicTableRecipeProvider;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.recipe.AbyssMagicTableRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class AbyssMagicTableRecipeProvider extends AbstractAbyssMagicTableRecipeProvider {
    public AbyssMagicTableRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "abyss_magic_table", registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        super.buildRecipes(output);
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            ItemStack ingot = new ItemStack(ProjectKItems.ABYSS_INGOT.get());
            AbyssEnergyData.applyToStack(ingot, definition.id(), definition.defaultAmount());
            addAbyssMagicTableRecipe(definition.id(),
                    new AbyssMagicTableRecipe(definition.id(), definition.defaultAmount(),
                            new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1), ingot));
        }
    }
}
