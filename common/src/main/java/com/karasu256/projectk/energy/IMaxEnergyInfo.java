package com.karasu256.projectk.energy;

import net.minecraft.nbt.CompoundTag;

import com.karasu256.projectk.energy.EnergyKeys;

public interface IMaxEnergyInfo extends IEnergyBlockEntitySync {

    long getBaseMaxEnergy();

    long getMaxEnergy();

    void setMaxEnergy(long maxEnergy);

    default long getTieredMaxEnergy(int tier) {
        int safeTier = Math.max(1, tier);
        if (safeTier <= 1) return getBaseMaxEnergy();
        return (long) (getBaseMaxEnergy() * Math.pow(2.5, safeTier - 1));
    }

    default void loadMaxEnergy(CompoundTag nbt) {
        long maxEnergy;
        if (nbt.contains(EnergyKeys.MAX_ENERGY.toString())) {
            maxEnergy = nbt.getLong(EnergyKeys.MAX_ENERGY.toString());
        } else if (this instanceof ITierInfo tierInfo) {
            maxEnergy = getTieredMaxEnergy(tierInfo.getTier());
        } else {
            maxEnergy = getBaseMaxEnergy();
        }
        setMaxEnergy(maxEnergy);
    }

    default void saveMaxEnergy(CompoundTag nbt) {
        nbt.putLong(EnergyKeys.MAX_ENERGY.toString(), getMaxEnergy());
    }
}
