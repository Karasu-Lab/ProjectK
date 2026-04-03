package com.karasu256.projectk;

import com.karasu256.projectk.data.AbyssEnchanterTierManager;
import com.karasu256.projectk.data.AbyssEnergySpawnRuleManager;
import com.karasu256.projectk.event.ModEvents;
import net.karasuniki.karasunikilib.api.registry.KRegistryScanner;

public final class ProjectK {
    public static final String MOD_ID = "projectk";

    public static void init() {
        KRegistryScanner.scan(MOD_ID);

        AbyssEnchanterTierManager.init();
        AbyssEnergySpawnRuleManager.init();
        ModEvents.init();
    }
}
