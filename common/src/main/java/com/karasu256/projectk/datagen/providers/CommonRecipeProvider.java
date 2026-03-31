package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.item.ProjectKItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommonRecipeProvider extends RecipeProvider {
    public CommonRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void generate(RecipeOutput output) {
        oreSmelting(output, List.of(ProjectKItems.RAW_KARASIUM.get()), RecipeCategory.MISC, ProjectKItems.KARASIUM.get(), 0.7f, 200, "karasium");
        oreBlasting(output, List.of(ProjectKItems.RAW_KARASIUM.get()), RecipeCategory.MISC, ProjectKItems.KARASIUM.get(), 0.7f, 100, "karasium");
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        generate(output);
    }
}
