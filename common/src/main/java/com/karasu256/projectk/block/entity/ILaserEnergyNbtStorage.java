package com.karasu256.projectk.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface ILaserEnergyNbtStorage {
    void readEnergies(CompoundTag nbt, HolderLookup.Provider provider);

    void writeEnergies(CompoundTag nbt, HolderLookup.Provider provider);

    void addOrIncrease(ResourceLocation energyId, long amount);
}
