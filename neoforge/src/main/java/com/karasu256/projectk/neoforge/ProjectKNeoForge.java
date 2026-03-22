package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.neoforge.integrations.IModIntegration;
import com.karasu256.projectk.neoforge.integrations.projecte.ProjectEIntegration;
import net.neoforged.fml.common.Mod;

import java.util.List;

@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge() {
        ProjectK.init();

        List<IModIntegration> modIntegrations = List.of(new ProjectEIntegration());
        modIntegrations.forEach(IModIntegration::bootStrap);
    }
}
