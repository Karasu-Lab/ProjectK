package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AbyssBraceletItem extends ProjectKItem {
    public static final long CAPACITY = 30000L;

    public AbyssBraceletItem(ProjectKItem.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        long amount = getEnergyAmount(stack);
        return (int) Math.min(13L, Math.round((amount / (double) CAPACITY) * 13.0));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        ResourceLocation id = data == null ? ProjectKEnergies.ABYSS.id() : data.energyId();
        return ProjectKEnergies.getDefinition(id).map(ProjectKEnergies.EnergyDefinition::color).orElse(0xFFFFFFFF);
    }

    private long getEnergyAmount(ItemStack stack) {
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        return data == null ? 0L : data.amountOrZero();
    }
}
