package com.karasu256.projectk.client.util;

import com.karasu256.projectk.energy.AbyssEnergyUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

public class PKColorUtils {
    public static final int OPAQUE = 0xFF000000;
    public static final int SEMI_TRANSPARENT = 0x80000000;

    private PKColorUtils() {
    }

    public static int getEnergyColor(ResourceLocation energyId, int alphaMask) {
        return AbyssEnergyUtils.getEnergyColor(energyId, alphaMask);
    }

    public static float[] unpack(int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        return new float[]{r, g, b, a};
    }

    public static void setShaderColor(int color) {
        float[] rgba = unpack(color);
        RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);
    }
}
