package com.karasu256.projectk.neoforge.config;

import com.karasu256.projectk.config.ProjectKModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ProjectKNeoForgeConfig {
    public static ProjectKModConfig CONFIG;

    public static void init(ModContainer container) {
        AutoConfig.register(ProjectKModConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ProjectKModConfig.class).getConfig();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (mc, previousScreen) -> AutoConfig.getConfigScreen(ProjectKModConfig.class, previousScreen).get()
            );
        }
    }
}
