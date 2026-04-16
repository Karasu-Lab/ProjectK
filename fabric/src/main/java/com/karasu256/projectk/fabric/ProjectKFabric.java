package com.karasu256.projectk.fabric;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.fabric.config.ProjectKFabricConfig;
import com.karasu256.projectk.fabric.integrations.FabricModIntegrationSupplier;
import com.karasu256.projectk.fabric.platform.FabricProjectKPlatform;
import com.karasu256.projectk.platform.PlatformServices;
import net.fabricmc.api.ModInitializer;
import net.karasuniki.karasunikilib.api.ModIntegrationBootstrapper;

public final class ProjectKFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlatformServices.register(new FabricProjectKPlatform());
        ProjectK.init();
        ProjectKFabricConfig.init();
        ModIntegrationBootstrapper.bootstrap(new FabricModIntegrationSupplier<>(
                "com.karasu256.projectk.fabric.integrations.modmenu.ModMenuIntegration"));
    }
}
