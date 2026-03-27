package com.karasu256.projectk.neoforge.integrations;

import net.karasuniki.karasunikilib.api.IModIntegration;
import net.neoforged.fml.ModList;

public interface INeoForgeModIntegration extends IModIntegration {
    static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    default boolean isModLoaded() {
        return isModLoaded(getModId());
    }
}
