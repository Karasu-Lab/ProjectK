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

    @SuppressWarnings("DataFlowIssue")
    public void addInBiomeInBlockCrafting(InBiomeInBlockCraftingRecipe recipe) {
        getOutput().accept(Id.id(getName() + "_" + recipe.biome().id().getNamespace() + "_" + recipe.inputs().getFirst()
                .count() + "_" + recipe.biome().id().getPath() + "_" + recipe.result().getItem().arch$registryName()
                .getNamespace() + "_" + recipe.result().getItem().arch$registryName().getPath()), recipe, null);
    }
}
