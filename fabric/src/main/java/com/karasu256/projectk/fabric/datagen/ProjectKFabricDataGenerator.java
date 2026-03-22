package com.karasu256.projectk.fabric.datagen;

import com.karasu256.projectk.datagen.ProjectKCommonDataGenerator;
import com.karasu256.projectk.fabric.datagen.providers.FabricModIntegrationProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ProjectKFabricDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        ProjectKCommonDataGenerator.gatherData(pack::addProvider, fabricDataGenerator.getRegistriesFuture());
        pack.addProvider(FabricModIntegrationProvider::new);
    }
}
