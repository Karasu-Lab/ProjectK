package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.integration.ModIntegrationBootstrapper;
import com.karasu256.projectk.neoforge.integrations.NeoForgeModIntegrationSupplier;
import net.neoforged.fml.common.Mod;

@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge() {
        ProjectK.init();
        ModIntegrationBootstrapper.bootstrap(new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.projecte.ProjectEIntegration"));
    }
}

