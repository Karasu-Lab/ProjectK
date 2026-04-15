package com.karasu256.projectk.api.datagen.impl;

import com.karasu256.projectk.recipe.AbyssSynthesizerRecipe;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractAbyssSynthesizerRecipeProvider extends AbstractProjectKRecipeProvider {
    public AbstractAbyssSynthesizerRecipeProvider(PackOutput packOutput, String name, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(packOutput, name, completableFuture);
    }

    public void addSynthesizerRecipe(AbyssSynthesizerRecipe recipe) {
        String resultPath = BuiltInRegistries.ITEM.getKey(recipe.result().getItem()).getPath();
        getOutput().accept(Id.id(resultPath + "_from_abyss_synthesizer"), recipe, null);
    }
}
