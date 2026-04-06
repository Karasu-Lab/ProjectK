package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.client.screen.EnergyBarRenderer;
import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class AbyssEnergyBarComponent implements ITooltipComponent {
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 8;

    private final ResourceLocation energyId;
    private final long amount;
    private final Long capacity;
    private final boolean active;

    public AbyssEnergyBarComponent(ResourceLocation energyId, long amount, Long capacity, boolean active) {
        this.energyId = energyId;
        this.amount = amount;
        this.capacity = capacity;
        this.active = active;
    }

    @Override
    public int getWidth() {
        return BAR_WIDTH;
    }

    @Override
    public int getHeight() {
        return BAR_HEIGHT;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        if (active) {
            ctx.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0x80FFD27F);
        }
        EnergyBarRenderer.renderFluidBarHorizontal(ctx, energyId, amount, capacity, x, y, BAR_WIDTH, BAR_HEIGHT);
    }
}
