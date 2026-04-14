package com.karasu256.projectk.energy;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AbyssEnergyUtils {
    public static int getEnergyColor(ResourceLocation energyId) {
        return ProjectKEnergies.getDefinition(energyId)
                .map(ProjectKEnergies.EnergyDefinition::color)
                .orElse(0xFFFFFFFF);
    }

    public static int getEnergyColor(ResourceLocation energyId, int alphaMask) {
        return getEnergyColor(energyId) | alphaMask;
    }

    public static int getEnergyColor(ItemStack stack) {
        return getEnergyColor(getEffectiveEnergyId(stack));
    }

    public static ResourceLocation getEffectiveEnergyId(ItemStack stack) {
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (data != null) {
            return data.energyId();
        }
        return ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL);
    }
}
