package com.karasu256.projectk;

import com.karasu256.projectk.event.ModEvents;
import com.karasu256.projectk.data.AbyssEnergySpawnRuleManager;
import net.karasuniki.karasunikilib.api.registry.KRegistryScanner;

public final class ProjectK {
    public static final String MOD_ID = "projectk";

    public static void init() {
        KRegistryScanner.scan(MOD_ID);

        AbyssEnergySpawnRuleManager.init();
        ModEvents.init();
    }
}
