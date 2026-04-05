package com.karasu256.projectk.compat.wthit;

public interface IWthitCustomEnergy {
    default boolean shouldShowDefaultEnergyTooltip() {
        return true;
    }
}
