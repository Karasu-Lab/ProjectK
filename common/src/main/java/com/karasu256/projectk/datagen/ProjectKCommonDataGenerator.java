package com.karasu256.projectk.datagen;

import com.karasu256.projectk.datagen.providers.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;

import java.util.concurrent.CompletableFuture;

public class ProjectKCommonDataGenerator {

    public static void gatherData(ProviderRegistry registry, CompletableFuture<HolderLookup.Provider> registries, boolean includeRecipes) {
        CommonBlockTagsProvider blockTagsProvider = registry.register(
                output -> new CommonBlockTagsProvider(output, registries));
        registry.register(output -> new CommonItemTagsProvider(output, registries, blockTagsProvider.contentsGetter()));
        registry.register(output -> new ProjectKEnchantmentProvider(output));
        registry.register(output -> new CommonEnchantmentTagsProvider(output, registries));
        registry.register(AbyssEnergySpawnRuleProvider::new);
        registry.register(output -> new AbyssEnchanterTierProvider(output));
        registry.register(output -> new AbyssLaserEmitterTierProvider(output));
        if (includeRecipes) {
            registry.register(output -> new CommonRecipeProvider(output, registries));
            registry.register(output -> new AbyssMagicTableRecipeProvider(output, registries));
            registry.register(output -> new AbyssAlchemyBlendRecipeProvider(output, registries));
            registry.register(output -> new AbyssSynthesizerRecipeProvider(output, registries));
            registry.register(output -> new InBiomeInBlockCraftingProvider(output, registries));
        }
        registry.register(output -> new ProjectKLootTableProvider(output, registries));
    }

    @FunctionalInterface
    public interface ProviderRegistry {
        <T extends DataProvider> T register(DataProvider.Factory<T> factory);
    }
}
