package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.datagen.providers.CommonRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class NeoForgeRecipeProvider extends RecipeProvider {
    public NeoForgeRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        CommonRecipeProvider.generate(output);
    }
}
