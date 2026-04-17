package com.karasu256.projectk.fabric.datagen.providers;

import com.karasu256.projectk.datagen.providers.recipe.CommonRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class FabricModIntegrationProvider extends FabricRecipeProvider {

    public FabricModIntegrationProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        CommonRecipeProvider.generate(exporter);
    }

    @Override
    @NotNull
    public String getName() {
        return "ProjectK Fabric Integration Recipes";
    }
}
