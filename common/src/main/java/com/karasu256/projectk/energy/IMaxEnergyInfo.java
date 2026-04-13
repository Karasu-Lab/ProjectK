package com.karasu256.projectk.energy;

import net.minecraft.nbt.CompoundTag;

public interface IMaxEnergyInfo extends IEnergyBlockEntitySync {

    long getMaxEnergy();

    void setMaxEnergy(long maxEnergy);

    default void loadMaxEnergy(CompoundTag nbt) {
        if (nbt.contains(EnergyKeys.MAX_ENERGY.toString())) {
            setMaxEnergy(nbt.getLong(EnergyKeys.MAX_ENERGY.toString()));
        }
    }

    default void saveMaxEnergy(CompoundTag nbt) {
        nbt.putLong(EnergyKeys.MAX_ENERGY.toString(), getMaxEnergy());
    }
}
