package com.karasu256.projectk.datagen.providers.lang;

import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.energy.ProjectKEnergies;

import java.util.function.BiConsumer;

public interface ProjectKLanguage {
    String getLocale();

    void addCommon(BiConsumer<String, String> adder);

    void addEnergy(ProjectKEnergies.EnergyDefinition definition, BiConsumer<String, String> adder);

    void addEnergyItem(ProjectKEnergies.EnergyDefinition definition, String energyName, BiConsumer<String, String> adder);

    void addEnergyBlock(ProjectKEnergies.EnergyDefinition definition, String energyName, BiConsumer<String, String> adder);

    void addBlockSet(String baseName, BlockMaterials material, String key, BiConsumer<String, String> adder);
}
