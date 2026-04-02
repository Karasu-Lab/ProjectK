package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.menu.AbyssMagicTableMenu;
import com.karasu256.projectk.utils.AbyssMagicTableInfo;
import com.karasu256.projectk.utils.Id;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AbyssMagicTableScreen extends AbstractContainerScreen<AbyssMagicTableMenu> {
    private static final ResourceLocation TEXTURE = Id.id("textures/gui/abyss_magic_table.png");

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
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderProgress(graphics);
        renderEnergy(graphics);
    }

    private void renderProgress(GuiGraphics graphics) {
        int width = AbyssMagicTableInfo.progressWidth(menu.getProgress(), menu.getMaxProgress(), 24);
        graphics.blit(TEXTURE, leftPos + 79, topPos + 34, 176, 0, width, 17);
    }

    private void renderEnergy(GuiGraphics graphics) {
        long energy = menu.getEnergy();
        long capacity = menu.getEnergyCapacity();
        int height = capacity <= 0 ? 0 : (int) Math.min((energy * 50L) / capacity, 50L);
        graphics.blit(TEXTURE, leftPos + 10, topPos + 20 + (50 - height), 176, 17, 8, height);
    }
}
