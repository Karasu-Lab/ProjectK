package com.karasu256.projectk.client.screen;

import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface ProgressBarRenderer {
    void render(GuiGraphics graphics, int leftPos, int topPos, int progress, int maxProgress);
}
