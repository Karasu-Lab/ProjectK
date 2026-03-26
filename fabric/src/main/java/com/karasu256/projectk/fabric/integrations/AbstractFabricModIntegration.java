package com.karasu256.projectk.fabric.integrations;


import com.karasu256.karasulab.karasucore.api.impl.AbstractModIntegration;
import net.fabricmc.loader.api.FabricLoader;

public abstract class AbstractFabricModIntegration extends AbstractModIntegration implements com.karasu256.karasulab.karasucore.api.IModIntegration {
    @Override
    public boolean isModLoaded() {
        return FabricLoader.getInstance().isModLoaded(getModId());
    }
}
