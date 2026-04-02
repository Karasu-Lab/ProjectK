package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.recipe.AbyssMagicTableRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommonRecipeProvider extends RecipeProvider {
    public CommonRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void generate(RecipeOutput output) {
        oreSmelting(output, List.of(ProjectKItems.RAW_KARASIUM.get()), RecipeCategory.MISC, ProjectKItems.KARASIUM.get(), 0.7f, 200, "karasium");
        oreBlasting(output, List.of(ProjectKItems.RAW_KARASIUM.get()), RecipeCategory.MISC, ProjectKItems.KARASIUM.get(), 0.7f, 100, "karasium");
        addAbyssMagicTableRecipes(output);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        generate(output);
        InBiomeInBlockCraftingProvider.addWitherRoseRecipe(output);
    }

    private static void addAbyssMagicTableRecipes(RecipeOutput output) {
        ItemStack abyssIngot = new ItemStack(ProjectKItems.ABYSS_INGOT.get());
        AbyssEnergyData.applyToStack(abyssIngot, ProjectKEnergies.ABYSS_ENERGY.get().getId(), 500L);
        AbyssMagicTableRecipe abyssRecipe = new AbyssMagicTableRecipe(
                ProjectKEnergies.ABYSS_ENERGY.get().getId(),
                500L,
                new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1),
                abyssIngot
        );
        output.accept(Id.id("abyss_ingot_from_magic_table"), abyssRecipe, null);

        ItemStack yinIngot = new ItemStack(ProjectKItems.YIN_ABYSS_INGOT.get());
        AbyssEnergyData.applyToStack(yinIngot, ProjectKEnergies.YIN_ABYSS_ENERGY.get().getId(), 500L);
        AbyssMagicTableRecipe yinRecipe = new AbyssMagicTableRecipe(
                ProjectKEnergies.YIN_ABYSS_ENERGY.get().getId(),
                500L,
                new IngredientStack(Ingredient.of(Items.IRON_INGOT), 1),
                yinIngot
        );
        output.accept(Id.id("yin_abyss_ingot_from_magic_table"), yinRecipe, null);
    }
}
