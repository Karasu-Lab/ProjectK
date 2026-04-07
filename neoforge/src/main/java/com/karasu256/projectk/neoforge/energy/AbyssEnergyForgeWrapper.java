package com.karasu256.projectk.neoforge.energy;

import com.karasu256.projectk.energy.IMaxEnergyInfo;
import com.karasu256.projectk.energy.IMultiEnergyStorage;
import com.karasu256.projectk.energy.ProjectKEnergy;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class AbyssEnergyForgeWrapper implements IEnergyStorage {
    private final IMultiEnergyStorage internal;
    private final IMaxEnergyInfo maxInfo;
    private static final String FORGE_ID = "forge";

    public AbyssEnergyForgeWrapper(IMultiEnergyStorage internal, IMaxEnergyInfo maxInfo) {
        this.internal = internal;
        this.maxInfo = maxInfo;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        ResourceLocation id = internal.getAbyssEnergyId();
        if (id == null)
            return 0;
        long aeAmount = ProjectKEnergy.toAbyssEnergy(FORGE_ID, maxReceive);
        long insertedAE = internal.insert(id, aeAmount, simulate);
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, insertedAE);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        ResourceLocation id = internal.getAbyssEnergyId();
        if (id == null)
            return 0;
        long aeAmount = ProjectKEnergy.toAbyssEnergy(FORGE_ID, maxExtract);
        long extractedAE = internal.extract(id, aeAmount, simulate);
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, extractedAE);
    }

    @Override
    public int getEnergyStored() {
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, internal.getEnergyAmount());
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) ProjectKEnergy.fromAbyssEnergy(FORGE_ID, maxInfo.getMaxEnergy());
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
