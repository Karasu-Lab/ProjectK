package com.karasu256.projectk.energy;

import net.karasuniki.karasunikilib.api.ModIntegrationRegistry;

public class ProjectKEnergy {
    public static long toAbyssEnergy(String modId, long modEnergy) {
        return ModIntegrationRegistry.getEnergy(modId).map(integration -> (long) (modEnergy * integration.getToAbyssRate())).orElse(modEnergy);
    }

    public static long fromAbyssEnergy(String modId, long abyssEnergy) {
        return ModIntegrationRegistry.getEnergy(modId).map(integration -> (long) (abyssEnergy * integration.getFromAbyssRate())).orElse(abyssEnergy);
    }
}
