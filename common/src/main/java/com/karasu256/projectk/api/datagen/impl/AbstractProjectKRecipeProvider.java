package com.karasu256.projectk.api.datagen.impl;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractProjectKRecipeProvider extends RecipeProvider {
    private RecipeOutput output;
    private final String name;

    public AbstractProjectKRecipeProvider(PackOutput packOutput,
                                          CompletableFuture<HolderLookup.Provider> completableFuture, String name) {
        super(packOutput, completableFuture);
        this.name = name;
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        this.output = recipeOutput;
    }

    @Override
    public String getName() {
        return name;
    }
}
