package com.karasu256.projectk.fabric.datagen;

import com.karasu256.projectk.datagen.ProjectKCommonDataGenerator;
import com.karasu256.projectk.datagen.providers.*;
import com.karasu256.projectk.datagen.providers.lang.ProjectKLanguageProvider;
import com.karasu256.projectk.datagen.providers.recipe.AbyssAlchemyBlendRecipeProvider;
import com.karasu256.projectk.datagen.providers.recipe.AbyssMagicTableRecipeProvider;
import com.karasu256.projectk.datagen.providers.recipe.AbyssSynthesizerRecipeProvider;
import com.karasu256.projectk.enchant.ProjectKEnchantmentBootstrap;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.fabric.datagen.providers.FabricModIntegrationProvider;
import com.karasu256.projectk.fabric.datagen.providers.ProjectKFabricModelProvider;
import com.karasu256.projectk.fabric.datagen.providers.ProjectKFabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;

public class ProjectKFabricDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        ProjectKEnergies.init();

        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        ProjectKCommonDataGenerator.gatherData(pack::addProvider, fabricDataGenerator.getRegistries(), false);
        pack.addProvider(FabricModIntegrationProvider::new);
        pack.addProvider(ProjectKFabricRecipeProvider::new);
        pack.addProvider(AbyssMagicTableRecipeProvider::new);
        pack.addProvider(AbyssAlchemyBlendRecipeProvider::new);
        pack.addProvider(ProjectKFabricModelProvider::new);
        pack.addProvider((DataProvider.Factory<ProjectKBlockModelProvider>) ProjectKBlockModelProvider::new);
        pack.addProvider((DataProvider.Factory<ProjectKLanguageProvider>) ProjectKLanguageProvider::new);

        ProjectKCommonDataGenerator.gatherClientData(pack::addProvider);

        pack.addProvider(InBiomeInBlockCraftingProvider::new);
        pack.addProvider(AbyssSynthesizerRecipeProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder builder) {
        builder.add(Registries.ENCHANTMENT, ProjectKEnchantmentBootstrap::bootstrap);
    }
}
