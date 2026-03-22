package com.karasu256.projectk.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class CommonRecipeProvider extends RecipeProvider {
    public CommonRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void generate(RecipeOutput output) {
        // Implement default common recipes here
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        generate(output);
    }
}
