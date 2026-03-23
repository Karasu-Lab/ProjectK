package com.karasu256.projectk.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    public ConfigScreen(){
        this(Component.translatable("gui.configscreen.title"));
    }

    protected ConfigScreen(Component component) {
        super(component);
    }
}
