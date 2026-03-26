package com.karasu256.projectk.fabric;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.integration.ModIntegrationBootstrapper;
import com.karasu256.projectk.fabric.config.ProjectKFabricConfig;
import com.karasu256.projectk.fabric.integrations.FabricModIntegrationSupplier;
import net.fabricmc.api.ModInitializer;

public final class ProjectKFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ProjectKFabricConfig.init();
        ProjectK.init();
        ModIntegrationBootstrapper.bootstrap(new FabricModIntegrationSupplier<>("com.karasu256.projectk.fabric.integrations.modmenu.ModMenuIntegration"));
    }
}
