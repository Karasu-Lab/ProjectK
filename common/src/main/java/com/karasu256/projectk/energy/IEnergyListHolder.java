package com.karasu256.projectk.energy;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IEnergyListHolder {
    List<EnergyEntry> getEnergyEntries();

    record EnergyEntry(ResourceLocation id, long amount, Long capacity, boolean active) {
    }
}
