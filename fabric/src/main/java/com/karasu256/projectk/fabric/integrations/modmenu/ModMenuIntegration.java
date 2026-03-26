package com.karasu256.projectk.fabric.integrations.modmenu;

import com.karasu256.projectk.config.ProjectKModConfig;
import com.karasu256.projectk.fabric.integrations.AbstractFabricModIntegration;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration extends AbstractFabricModIntegration implements ModMenuApi {
    public static final String MOD_ID = "modmenu";

    @Override
    public void bootstrap() {
        super.bootstrap();
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(ProjectKModConfig.class, Minecraft.getInstance().screen).get();
    }
}
