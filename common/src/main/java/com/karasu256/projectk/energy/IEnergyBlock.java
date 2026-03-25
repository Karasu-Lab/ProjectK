package com.karasu256.projectk.energy;

public interface IEnergyBlock<T extends IEnergy> extends IProjectKEnergy {
    T getEnergyType();
}
