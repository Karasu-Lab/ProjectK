package com.karasu256.projectk.fabric;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.config.ProjectKModConfig;
import com.karasu256.projectk.fabric.integrations.FabricModIntegrationSupplier;
import com.karasu256.projectk.integration.ModIntegrationBootstrapper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public final class ProjectKFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(ProjectKModConfig.class, GsonConfigSerializer::new);
        ProjectK.init();
        ModIntegrationBootstrapper.bootstrap(new FabricModIntegrationSupplier<>("com.karasu256.projectk.fabric.integrations.modmenu.ModMenuIntegration"));
    }
}
