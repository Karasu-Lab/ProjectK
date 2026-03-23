package com.karasu256.projectk.fabric.integrations.modmenu;

import com.karasu256.projectk.client.screen.ConfigScreen;
import com.karasu256.projectk.fabric.integrations.AbstractFabricModIntegration;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration extends AbstractFabricModIntegration implements ModMenuApi {
    public static final String MOD_ID = "modmenu";

    @Override
    public void onBootstrap() {

    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new ConfigScreen();
    }
}
