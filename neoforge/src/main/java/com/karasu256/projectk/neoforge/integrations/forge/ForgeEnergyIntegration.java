package com.karasu256.projectk.neoforge.integrations.forge;

import com.karasu256.projectk.api.integration.IEnergyIntegration;
import com.karasu256.projectk.neoforge.integrations.AbstractNeoForgeModIntegration;

public class ForgeEnergyIntegration extends AbstractNeoForgeModIntegration implements IEnergyIntegration {
    public static final String MOD_ID = "forge";

    @Override
    public double getToAbyssRate() {
        return 0.1;
    }

    @Override
    public double getFromAbyssRate() {
        return 10.0;
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }
}
