package com.karasu256.projectk.fabric;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.fabric.integrations.FabricModIntegrationSupplier;
import com.karasu256.projectk.integration.ModIntegrationBootstrapper;
import net.fabricmc.api.ModInitializer;

public final class ProjectKFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ProjectK.init();
        ModIntegrationBootstrapper.bootstrap(new FabricModIntegrationSupplier<>("com.karasu256.projectk.fabric.integrations.modmenu.ModMenuIntegration"));
    }
}
