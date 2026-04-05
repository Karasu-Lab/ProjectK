package com.karasu256.projectk.energy;

import net.minecraft.nbt.CompoundTag;

public interface IMaxEnergyInfo {
    String MAX_ENERGY_KEY = "max_energy";

    long getBaseMaxEnergy();

    long getMaxEnergy();

    void setMaxEnergy(long maxEnergy);

    default long getTieredMaxEnergy(int tier) {
        int safeTier = Math.max(1, tier);
        return Math.max(0L, getBaseMaxEnergy() * safeTier);
    }

    default void loadMaxEnergy(CompoundTag nbt) {
        long maxEnergy;
        if (nbt.contains(MAX_ENERGY_KEY)) {
            maxEnergy = nbt.getLong(MAX_ENERGY_KEY);
        } else if (this instanceof ITierInfo tierInfo) {
            maxEnergy = getTieredMaxEnergy(tierInfo.getTier());
        } else {
            maxEnergy = getBaseMaxEnergy();
        }
        setMaxEnergy(maxEnergy);
    }

    default void saveMaxEnergy(CompoundTag nbt) {
        nbt.putLong(MAX_ENERGY_KEY, getMaxEnergy());
    }
}
