package com.karasu256.projectk.neoforge.integrations;

import com.karasu256.projectk.integration.AbstractModIntegration;
import net.neoforged.fml.ModList;

public abstract class AbstractNeoForgeModIntegration extends AbstractModIntegration {
    @Override
    public boolean isModLoaded() {
        return ModList.get().isLoaded(getModId());
    }
}
