package com.karasu256.projectk.energy;

public record AbyssEnergy(long value) implements IEnergy {
    @Override
    public long getValue() {
        return value;
    }
}
