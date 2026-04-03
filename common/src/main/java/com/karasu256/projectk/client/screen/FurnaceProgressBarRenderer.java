package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.utils.AbyssMagicTableInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class FurnaceProgressBarRenderer implements ProgressBarRenderer {
    private static final int WIDTH = 24;
    private static final int HEIGHT = 17;
    private static final ResourceLocation PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    private final int offsetX;
    private final int offsetY;

    public FurnaceProgressBarRenderer(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void render(GuiGraphics graphics, int leftPos, int topPos, int progress, int maxProgress) {
        int width = AbyssMagicTableInfo.progressWidth(progress, maxProgress, WIDTH);
        if (width <= 0) {
            return;
        }
        graphics.blitSprite(PROGRESS_SPRITE, WIDTH, HEIGHT, 0, 0, leftPos + offsetX, topPos + offsetY, width, HEIGHT);
    }
}
