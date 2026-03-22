package com.karasu256.projectk.datagen;

import com.karasu256.projectk.datagen.providers.CommonBlockTagsProvider;
import com.karasu256.projectk.datagen.providers.CommonItemTagsProvider;
import com.karasu256.projectk.datagen.providers.CommonRecipeProvider;
import com.karasu256.projectk.datagen.providers.ProjectKLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;

import java.util.concurrent.CompletableFuture;

public class ProjectKCommonDataGenerator {

    public static void gatherData(ProviderRegistry registry, CompletableFuture<HolderLookup.Provider> registries, boolean includeRecipes) {
        CommonBlockTagsProvider blockTagsProvider = registry.register(output -> new CommonBlockTagsProvider(output, registries));
        registry.register(output -> new CommonItemTagsProvider(output, registries, blockTagsProvider.contentsGetter()));
        if (includeRecipes) {
            registry.register(output -> new CommonRecipeProvider(output, registries));
        }
        registry.register(output -> new ProjectKLootTableProvider(output, registries));
    }

    @FunctionalInterface
    public interface ProviderRegistry {
        <T extends DataProvider> T register(DataProvider.Factory<T> factory);
    }
}
