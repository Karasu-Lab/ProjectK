package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.menu.AbyssEnchantRemoverMenu;
import com.karasu256.projectk.utils.Id;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AbyssEnchantRemoverScreen extends AbstractContainerScreen<AbyssEnchantRemoverMenu> {
    private static final ResourceLocation TEXTURE = Id.id("textures/gui/abyss_enchant_remover.png");

    public AbyssEnchantRemoverScreen(AbyssEnchantRemoverMenu menu, Inventory inventory, Component title) {
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
    }
}
