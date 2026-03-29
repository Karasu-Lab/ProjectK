package com.karasu256.projectk.neoforge.energy;

import com.karasu256.projectk.block.entity.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.energy.ProjectKEnergy;
import net.neoforged.neoforge.energy.IEnergyStorage;
 
public class AbyssEnergyForgeWrapper implements IEnergyStorage {
    private final AbstractPKEnergyBlockEntity<?> internal;
    private static final String FORGE_ID = "forge";
 
    public AbyssEnergyForgeWrapper(AbstractPKEnergyBlockEntity<?> internal) {
        this.internal = internal;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        long aeAmount = ProjectKEnergy.toAbyssEnergy(FORGE_ID, maxReceive);
        long insertedAE = internal.insert(internal.getEnergyType().getId(), aeAmount, simulate);
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, insertedAE);
    }
 
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        long aeAmount = ProjectKEnergy.toAbyssEnergy(FORGE_ID, maxExtract);
        long extractedAE = internal.extract(internal.getEnergyType().getId(), aeAmount, simulate);
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, extractedAE);
    }
 
    @Override
    public int getEnergyStored() {
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, internal.getAmount());
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
