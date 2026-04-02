package com.karasu256.projectk.fabric;

import net.karasuniki.karasunikilib.api.ModIntegrationBootstrapper;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.fabric.config.ProjectKFabricConfig;
import com.karasu256.projectk.fabric.integrations.FabricModIntegrationSupplier;
import com.karasu256.projectk.platform.PlatformServices;
import com.karasu256.projectk.fabric.platform.FabricProjectKPlatform;
import net.fabricmc.api.ModInitializer;

public final class ProjectKFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlatformServices.register(new FabricProjectKPlatform());
        ProjectKFabricConfig.init();
        ProjectK.init();
        ModIntegrationBootstrapper.bootstrap(new FabricModIntegrationSupplier<>("com.karasu256.projectk.fabric.integrations.modmenu.ModMenuIntegration"));
    }
}
