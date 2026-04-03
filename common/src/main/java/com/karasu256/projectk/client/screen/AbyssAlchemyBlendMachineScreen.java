package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.menu.AbyssAlchemyBlendMachineMenu;
import com.karasu256.projectk.utils.Id;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AbyssAlchemyBlendMachineScreen extends AbstractContainerScreen<AbyssAlchemyBlendMachineMenu> {
    private static final ResourceLocation TEXTURE = Id.id("textures/gui/abyss_alchemy_blend_machine.png");
    private static final ProgressBarRenderer PROGRESS_BAR = new FurnaceProgressBarRenderer(79, 34);

    public AbyssAlchemyBlendMachineScreen(AbyssAlchemyBlendMachineMenu menu, Inventory inventory, Component title) {
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
        renderEnergies(graphics);
    }

    private void renderProgress(GuiGraphics graphics) {
        PROGRESS_BAR.render(graphics, leftPos, topPos, menu.getProgress(), menu.getMaxProgress());
    }

    private void renderEnergies(GuiGraphics graphics) {
        EnergyBarRenderer.renderFluidBar(graphics, menu.getEnergyId1(), menu.getEnergyAmount1(), menu.getEnergyCapacity1(), leftPos + 10, topPos + 20, 8, 50);
        EnergyBarRenderer.renderFluidBar(graphics, menu.getEnergyId2(), menu.getEnergyAmount2(), menu.getEnergyCapacity2(), leftPos + 158, topPos + 20, 8, 50);
    }

    private void renderEnergyTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isHovering(10, 20, 8, 50, mouseX, mouseY)) {
            var tooltip = EnergyBarRenderer.toolTip(menu.getEnergyId1(), menu.getEnergyAmount1(), menu.getEnergyCapacity1());
            if (!tooltip.isEmpty()) {
                graphics.renderTooltip(font, tooltip, mouseX, mouseY);
            }
            return;
        }
        if (isHovering(158, 20, 8, 50, mouseX, mouseY)) {
            var tooltip = EnergyBarRenderer.toolTip(menu.getEnergyId2(), menu.getEnergyAmount2(), menu.getEnergyCapacity2());
            if (!tooltip.isEmpty()) {
                graphics.renderTooltip(font, tooltip, mouseX, mouseY);
            }
        }
    }
}
