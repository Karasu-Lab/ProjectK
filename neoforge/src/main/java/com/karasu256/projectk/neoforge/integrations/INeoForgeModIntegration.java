package com.karasu256.projectk.neoforge.integrations;

import com.karasu256.projectk.integration.IModIntegration;
import net.neoforged.fml.ModList;

public interface INeoForgeModIntegration extends IModIntegration {
    static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    default boolean isModLoaded() {
        return ModList.get().isLoaded(getModId());
    }
}
