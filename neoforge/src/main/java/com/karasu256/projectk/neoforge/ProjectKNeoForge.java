package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.integration.ModIntegrationBootstrapper;
import com.karasu256.projectk.neoforge.config.ProjectKNeoForgeConfig;
import com.karasu256.projectk.neoforge.integrations.NeoForgeModIntegrationSupplier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge(ModContainer container) {
        ProjectK.init();
        ProjectKNeoForgeConfig.init(container);
        ModIntegrationBootstrapper.bootstrap(
            new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.projecte.ProjectEIntegration"),
            new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.mekanism.MekanismIntegration"),
            new NeoForgeModIntegrationSupplier<>("com.karasu256.projectk.neoforge.integrations.forge.ForgeEnergyIntegration")
        );
    }
}
