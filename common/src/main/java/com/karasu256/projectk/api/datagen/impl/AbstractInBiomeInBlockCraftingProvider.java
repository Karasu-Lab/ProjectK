package com.karasu256.projectk.api.datagen.impl;

import com.karasu256.projectk.recipe.InBiomeInBlockCraftingRecipe;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractInBiomeInBlockCraftingProvider extends AbstractProjectKRecipeProvider {
    public AbstractInBiomeInBlockCraftingProvider(PackOutput packOutput, String name, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(packOutput, name, completableFuture);
    }

    public void addInBiomeInBlockCrafting(InBiomeInBlockCraftingRecipe recipe) {
        getOutput().accept(Id.id("in_biome_in_block_" + recipe.biome().id().getNamespace() + "_" + recipe.biome().id()
                .getPath() + "_" + recipe.result().getItem().arch$registryName()), recipe, null);
    }
}
