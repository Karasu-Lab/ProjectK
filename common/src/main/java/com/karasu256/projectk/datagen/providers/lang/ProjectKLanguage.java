package com.karasu256.projectk.datagen.providers.lang;

import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.energy.ProjectKEnergies;

public interface ProjectKLanguage {
    String getLocale();

    void addCommon(TranslationAdder adder);

    void addEnergy(ProjectKEnergies.EnergyDefinition definition, TranslationAdder adder);

    void addEnergyItem(ProjectKEnergies.EnergyDefinition definition, String energyName, TranslationAdder adder);

    void addEnergyBlock(ProjectKEnergies.EnergyDefinition definition, String energyName, TranslationAdder adder);

    void addBlockSet(String baseName, BlockMaterials material, String key, TranslationAdder adder);
}
