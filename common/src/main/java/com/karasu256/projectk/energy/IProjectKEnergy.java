package com.karasu256.projectk.energy;

public interface IProjectKEnergy {
    long insert(long maxAmount, boolean simulate);
    long extract(long maxAmount, boolean simulate);
    long getAmount();
    long getCapacity();
}
