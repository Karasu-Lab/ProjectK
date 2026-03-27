package com.karasu256.projectk.neoforge.integrations.mekanism;

import com.karasu256.projectk.neoforge.integrations.AbstractNeoForgeModIntegration;
import net.karasuniki.karasunikilib.api.IEnergyIntegration;

@SuppressWarnings("unused")
public class MekanismIntegration extends AbstractNeoForgeModIntegration implements IEnergyIntegration {
    public static final String MOD_ID = "mekanism";

    @Override
    public double getToAbyssRate() {
        return 0.04;
    }

    @Override
    public double getFromAbyssRate() {
        return 25.0;
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }
}
