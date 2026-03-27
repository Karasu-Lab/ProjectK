package com.karasu256.projectk.fabric.integrations;


import net.karasuniki.karasunikilib.api.impl.AbstractModIntegration;
import net.fabricmc.loader.api.FabricLoader;

public abstract class AbstractFabricModIntegration extends AbstractModIntegration implements net.karasuniki.karasunikilib.api.IModIntegration {
    @Override
    public boolean isModLoaded() {
        return FabricLoader.getInstance().isModLoaded(getModId());
    }
}
