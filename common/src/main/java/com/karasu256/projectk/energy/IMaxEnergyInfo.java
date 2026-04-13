package com.karasu256.projectk.energy;

import net.minecraft.nbt.CompoundTag;

import com.karasu256.projectk.energy.EnergyKeys;

public interface IMaxEnergyInfo extends IEnergyBlockEntitySync {

    long getBaseMaxEnergy();

    long getMaxEnergy();

    void setMaxEnergy(long maxEnergy);

    long getTieredMaxEnergy(int tier);

    default void loadMaxEnergy(CompoundTag nbt) {
        if (nbt.contains(EnergyKeys.MAX_ENERGY.toString())) {
            setMaxEnergy(nbt.getLong(EnergyKeys.MAX_ENERGY.toString()));
        }
    }

    default void saveMaxEnergy(CompoundTag nbt) {
        nbt.putLong(EnergyKeys.MAX_ENERGY.toString(), getMaxEnergy());
    }
}
