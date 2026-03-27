package com.karasu256.projectk.neoforge.energy;

import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.energy.ProjectKEnergy;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class AbyssEnergyForgeWrapper implements IEnergyStorage {
    private final IAbyssEnergy internal;
    private static final String FORGE_ID = "forge";

    public AbyssEnergyForgeWrapper(IAbyssEnergy internal) {
        this.internal = internal;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        long aeAmount = ProjectKEnergy.toAbyssEnergy(FORGE_ID, maxReceive);
        long insertedAE = internal.insert(aeAmount, simulate);
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, insertedAE);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        long aeAmount = ProjectKEnergy.toAbyssEnergy(FORGE_ID, maxExtract);
        long extractedAE = internal.extract(aeAmount, simulate);
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, extractedAE);
    }

    @Override
    public int getEnergyStored() {
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, internal.getValue());
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, internal.getCapacity());
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
