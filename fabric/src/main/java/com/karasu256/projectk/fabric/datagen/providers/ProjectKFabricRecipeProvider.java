package com.karasu256.projectk.fabric.datagen.providers;

import com.karasu256.projectk.datagen.providers.CommonRecipeProvider;
import com.karasu256.projectk.datagen.providers.InBiomeInBlockCraftingProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ProjectKFabricRecipeProvider extends FabricRecipeProvider {
    public ProjectKFabricRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        CommonRecipeProvider.generate(exporter);
    }

    @Override
    @NotNull
    public String getName() {
        return "ProjectK Fabric Recipes";
    }
}
