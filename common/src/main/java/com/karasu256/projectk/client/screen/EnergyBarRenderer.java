package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.ProjectK;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class EnergyBarRenderer {
    private static final int SPRITE_SIZE = 16;

    private EnergyBarRenderer() {
    }

    public static void renderFluidBar(GuiGraphics graphics, ResourceLocation energyId, long amount, long capacity, int x, int y, int width, int height) {
        if (energyId == null || capacity <= 0) {
            return;
        }
        int fill = (int) Math.min((amount * (long) height) / capacity, height);
        if (fill <= 0) {
            return;
        }
        ResourceLocation sprite = ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(), "block/fluid_" + energyId.getPath() + "_still");
        int spriteFill = Math.max(1, (int) Math.min((fill * SPRITE_SIZE) / (long) height, SPRITE_SIZE));
        int spriteV = SPRITE_SIZE - spriteFill;
        int yOffset = height - fill;
        graphics.blitSprite(sprite, SPRITE_SIZE, SPRITE_SIZE, 0, spriteV, x, y + yOffset, width, fill);
    }
}
