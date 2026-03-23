package com.karasu256.projectk.fabric.integrations;

import com.karasu256.projectk.integration.IModIntegration;
import net.fabricmc.loader.api.FabricLoader;

public interface IFabricModIntegration extends IModIntegration {
    static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    default boolean isModLoaded() {
        return FabricLoader.getInstance().isModLoaded(getModId());
    }
}
