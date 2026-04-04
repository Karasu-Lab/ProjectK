package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.menu.AbyssStorageMenu;
import com.karasu256.projectk.utils.Id;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AbyssStorageScreen extends AbstractContainerScreen<AbyssStorageMenu> {
    private static final ResourceLocation TEXTURE = Id.id("textures/gui/abyss_storage.png");
    private static final int BAR_Y = 20;
    private static final int BAR_WIDTH = 8;
    private static final int BAR_HEIGHT = 50;
    private static final int BAR_START_X = 110;
    private static final int BAR_SPACING = 18;

    public AbyssStorageScreen(AbyssStorageMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderEnergyBars(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < 3; i++) {
                if (isHoveringBar(i, (int) mouseX, (int) mouseY)) {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderEnergyBars(GuiGraphics graphics, int mouseX, int mouseY) {
        for (int i = 0; i < 3; i++) {
            int x = leftPos + BAR_START_X + BAR_SPACING * i;
            int y = topPos + BAR_Y;
            long amount = menu.getEnergyAmount(i);
            long capacity = menu.getCapacity();
            var energyId = menu.getEnergyId(i);
            if (energyId == null || capacity <= 0) {
                continue;
            }
            if (menu.getActiveIndex() == i) {
                graphics.fill(x - 2, y - 2, x + BAR_WIDTH + 2, y + BAR_HEIGHT + 2, 0x80FFD27F);
            }
            EnergyBarRenderer.renderFluidBar(graphics, energyId, amount, capacity, x, y, BAR_WIDTH, BAR_HEIGHT);
        }
        renderEnergyTooltip(graphics, mouseX, mouseY);
    }

    private void renderEnergyTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        for (int i = 0; i < 3; i++) {
            if (isHoveringBar(i, mouseX, mouseY)) {
                var tooltip = EnergyBarRenderer.toolTip(menu.getEnergyId(i), menu.getEnergyAmount(i), menu.getCapacity());
                if (!tooltip.isEmpty()) {
                    graphics.renderTooltip(font, tooltip, mouseX, mouseY);
                }
                return;
            }
        }
    }

    private boolean isHoveringBar(int index, int mouseX, int mouseY) {
        int x = BAR_START_X + BAR_SPACING * index;
        int y = BAR_Y;
        return isHovering(x, y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY);
    }
}
