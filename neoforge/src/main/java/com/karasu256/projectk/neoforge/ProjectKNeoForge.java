package com.karasu256.projectk.neoforge;

import com.karasu256.projectk.ProjectK;
import net.neoforged.fml.common.Mod;

@Mod(ProjectK.MOD_ID)
public final class ProjectKNeoForge {
    public ProjectKNeoForge() {
        ProjectK.init();
    }
}
