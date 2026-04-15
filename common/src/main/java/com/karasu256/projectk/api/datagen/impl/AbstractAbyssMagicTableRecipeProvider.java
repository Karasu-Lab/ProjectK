package com.karasu256.projectk.api.datagen.impl;

import com.karasu256.projectk.recipe.AbyssMagicTableRecipe;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractAbyssMagicTableRecipeProvider extends AbstractProjectKRecipeProvider {
    public AbstractAbyssMagicTableRecipeProvider(PackOutput output, String name, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, name, registries);
    }

    public void addAbyssMagicTableRecipe(ResourceLocation energyId, AbyssMagicTableRecipe recipe) {
        String resultPath = BuiltInRegistries.ITEM.getKey(recipe.result().getItem()).getPath();
        String energyPath = energyId.getPath();
        getOutput().accept(Id.id(resultPath + "_" + energyPath + "_from_magic_table"), recipe, null);
    }
}
