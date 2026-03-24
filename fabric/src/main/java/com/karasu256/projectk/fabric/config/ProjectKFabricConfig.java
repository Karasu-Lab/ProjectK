package com.karasu256.projectk.fabric.config;

import com.karasu256.projectk.config.ProjectKModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class ProjectKFabricConfig {
    public static void init() {
        AutoConfig.register(ProjectKModConfig.class, GsonConfigSerializer::new);
    }
}