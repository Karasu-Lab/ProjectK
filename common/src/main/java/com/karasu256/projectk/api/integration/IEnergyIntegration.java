package com.karasu256.projectk.api.integration;

/**
 * Interface for mod integrations that handle energy.
 */
public interface IEnergyIntegration extends IModIntegration {
    /**
     * Returns the conversion rate from this mod's energy to AbyssEnergy (AE).
     * 1 ModEnergy * getToAbyssRate() = AE
     * Example: If 10 FE = 1 AE, then getToAbyssRate() should return 0.1.
     */
    double getToAbyssRate();

    /**
     * Returns the conversion rate from AbyssEnergy (AE) to this mod's energy.
     * 1 AE * getFromAbyssRate() = ModEnergy
     * Example: If 1 AE = 10 FE, then getFromAbyssRate() should return 10.0.
     */
    default double getFromAbyssRate() {
        return 1.0 / getToAbyssRate();
    }
}
