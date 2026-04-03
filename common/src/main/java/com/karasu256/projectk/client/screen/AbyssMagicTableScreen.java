package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.menu.AbyssMagicTableMenu;
import com.karasu256.projectk.utils.Id;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AbyssMagicTableScreen extends AbstractContainerScreen<AbyssMagicTableMenu> {
    private static final ResourceLocation TEXTURE = Id.id("textures/gui/abyss_magic_table.png");
    private static final ProgressBarRenderer PROGRESS_BAR = new FurnaceProgressBarRenderer(79, 34);

    public AbyssMagicTableScreen(AbyssMagicTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderEnergyTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderProgress(graphics);
        renderEnergy(graphics);
    }

    private void renderProgress(GuiGraphics graphics) {
        PROGRESS_BAR.render(graphics, leftPos, topPos, menu.getProgress(), menu.getMaxProgress());
    }

    private void renderEnergy(GuiGraphics graphics) {
        EnergyBarRenderer.renderFluidBar(graphics, menu.getEnergyId(), menu.getEnergy(), menu.getEnergyCapacity(), leftPos + 10, topPos + 20, 8, 50);
    }

    private void renderEnergyTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isHovering(10, 20, 8, 50, mouseX, mouseY)) {
            var tooltip = EnergyBarRenderer.toolTip(menu.getEnergyId(), menu.getEnergy(), menu.getEnergyCapacity());
            if (!tooltip.isEmpty()) {
                graphics.renderTooltip(font, tooltip, mouseX, mouseY);
            }
        }
    }
}
