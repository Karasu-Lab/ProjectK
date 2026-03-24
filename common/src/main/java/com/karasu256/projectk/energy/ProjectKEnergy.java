package com.karasu256.projectk.energy;

import com.karasu256.projectk.api.integration.ModIntegrationRegistry;

/**
 * Utility class for AbyssEnergy conversion.
 */
public class ProjectKEnergy {
    /**
     * Converts a value from a specified mod's energy unit to AbyssEnergy (AE).
     */
    public static long toAbyssEnergy(String modId, long modEnergy) {
        return ModIntegrationRegistry.getEnergy(modId)
                .map(integration -> (long) (modEnergy * integration.getToAbyssRate()))
                .orElse(modEnergy);
    }

    /**
     * Converts a value from AbyssEnergy (AE) to a specified mod's energy unit.
     */
    public static long fromAbyssEnergy(String modId, long abyssEnergy) {
        return ModIntegrationRegistry.getEnergy(modId)
                .map(integration -> (long) (abyssEnergy * integration.getFromAbyssRate()))
                .orElse(abyssEnergy);
    }
}
