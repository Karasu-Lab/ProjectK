package com.karasu256.projectk.fabric.integrations;

import com.karasu256.projectk.api.integration.IModIntegration;
import net.fabricmc.loader.api.FabricLoader;

public interface IFabricModIntegration extends IModIntegration {
    static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    default boolean isModLoaded() {
        return isModLoaded(getModId());
    }
}
