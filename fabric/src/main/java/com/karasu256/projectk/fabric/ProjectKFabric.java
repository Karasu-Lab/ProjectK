package com.karasu256.projectk.fabric;

import com.karasu256.projectk.ProjectK;
import net.fabricmc.api.ModInitializer;

public final class ProjectKFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ProjectK.init();
    }
}
