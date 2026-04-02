package com.karasu256.projectk.api.fluid;

public interface AbyssEnergyFluidConversion {
    long DEFAULT_ENERGY_PER_BUCKET = 1000L;

    default long getEnergyPerBucket() {
        return DEFAULT_ENERGY_PER_BUCKET;
    }
}
