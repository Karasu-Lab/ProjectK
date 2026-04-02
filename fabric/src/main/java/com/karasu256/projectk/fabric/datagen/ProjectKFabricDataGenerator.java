package com.karasu256.projectk.fabric.datagen;

import com.karasu256.projectk.datagen.ProjectKCommonDataGenerator;
import com.karasu256.projectk.datagen.providers.AbyssMagicTableRecipeProvider;
import com.karasu256.projectk.datagen.providers.InBiomeInBlockCraftingProvider;
import com.karasu256.projectk.datagen.providers.ProjectKBlockModelProvider;
import com.karasu256.projectk.datagen.providers.ProjectKLanguageProvider;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.fabric.datagen.providers.FabricModIntegrationProvider;
import com.karasu256.projectk.fabric.datagen.providers.ProjectKFabricModelProvider;
import com.karasu256.projectk.fabric.datagen.providers.ProjectKFabricRecipeProvider;
import net.minecraft.data.DataProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ProjectKFabricDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        ProjectKEnergies.init();

        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        ProjectKCommonDataGenerator.gatherData(pack::addProvider, fabricDataGenerator.getRegistries(), false);
        pack.addProvider(FabricModIntegrationProvider::new);
        pack.addProvider(ProjectKFabricRecipeProvider::new);
        pack.addProvider(AbyssMagicTableRecipeProvider::new);
        pack.addProvider(ProjectKFabricModelProvider::new);
        pack.addProvider((DataProvider.Factory<ProjectKBlockModelProvider>) ProjectKBlockModelProvider::new);
        pack.addProvider((DataProvider.Factory<ProjectKLanguageProvider>) ProjectKLanguageProvider::new);
        pack.addProvider(InBiomeInBlockCraftingProvider::new);
    }
}
