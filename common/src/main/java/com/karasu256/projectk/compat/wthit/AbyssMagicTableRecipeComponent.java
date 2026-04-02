package com.karasu256.projectk.compat.wthit;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.ProgressArrowComponent;
import mcp.mobius.waila.api.component.SpacingComponent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class AbyssMagicTableRecipeComponent implements ITooltipComponent {
    private final ITooltipComponent input;
    private final ITooltipComponent progress;
    private final ITooltipComponent output;
    private final int spacing;
    private int width = -1;
    private int height = -1;

    public AbyssMagicTableRecipeComponent(ItemStack inputStack, float progressRatio, ItemStack outputStack) {
        this(inputStack, progressRatio, outputStack, 4);
    }

    public AbyssMagicTableRecipeComponent(ItemStack inputStack, float progressRatio, ItemStack outputStack, int spacing) {
        this.input = inputStack.isEmpty() ? new SpacingComponent(18, 18) : new ItemComponent(inputStack);
        this.progress = new ProgressArrowComponent(progressRatio);
        this.output = new ItemComponent(outputStack);
        this.spacing = Math.max(0, spacing);
    }

    @Override
    public int getWidth() {
        if (width == -1) {
            width = computeWidth();
        }
        return width;
    }

    @Override
    public int getHeight() {
        if (height == -1) {
            height = Math.max(input.getHeight(), Math.max(progress.getHeight(), output.getHeight()));
        }
        return height;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        int cursor = x;
        boolean hasAny = false;
        int next = renderComponent(ctx, input, cursor, y, delta, hasAny);
        hasAny = hasAny || next != cursor;
        cursor = next;
        next = renderComponent(ctx, progress, cursor, y, delta, hasAny);
        hasAny = hasAny || next != cursor;
        cursor = next;
        renderComponent(ctx, output, cursor, y, delta, hasAny);
    }

    private int computeWidth() {
        int total = 0;
        boolean hasAny = false;
        total += componentWidth(input, hasAny);
        hasAny = hasAny || input.getWidth() > 0;
        total += componentWidth(progress, hasAny);
        hasAny = hasAny || progress.getWidth() > 0;
        total += componentWidth(output, hasAny);
        return total;
    }

    private int componentWidth(ITooltipComponent component, boolean addSpacing) {
        int compWidth = component.getWidth();
        if (compWidth <= 0) {
            return 0;
        }
        return compWidth + (addSpacing ? spacing : 0);
    }

    private int renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, DeltaTracker delta, boolean addSpacing) {
        int compWidth = component.getWidth();
        int compHeight = component.getHeight();
        if (compWidth <= 0 || compHeight <= 0) {
            return x;
        }
        int offset = getHeight() > compHeight ? (getHeight() - compHeight) / 2 : 0;
        int renderX = addSpacing ? x + spacing : x;
        IClientApiService.INSTANCE.renderComponent(ctx, component, renderX, y + offset, delta);
        return renderX + compWidth;
    }
}
