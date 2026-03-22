package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.datagen.providers.CommonRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NeoForgeModIntegrationProvider extends RecipeProvider implements IConditionBuilder {

    public NeoForgeModIntegrationProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        CommonRecipeProvider.generate(output);
        // Implement NeoForge-specific recipes with conditions here
    }
}
