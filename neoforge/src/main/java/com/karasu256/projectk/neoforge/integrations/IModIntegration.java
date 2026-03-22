package com.karasu256.projectk.neoforge.integrations;

import net.neoforged.fml.ModList;

public interface IModIntegration {
    void bootStrap();

    void onBootStrap();

    default boolean isModLoaded() {
        return ModList.get().isLoaded(getModId());
    }

    String getModId();
}
