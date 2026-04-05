package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.screen.EnergyBarRenderer;
import com.karasu256.projectk.item.custom.AbyssEnergyItem;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AbyssMagicTableCategory implements DisplayCategory<AbyssMagicTableDisplay> {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 70;

    @Override
    public CategoryIdentifier<? extends AbyssMagicTableDisplay> getCategoryIdentifier() {
        return AbyssMagicTableDisplay.ID;
    }

    @Override
    public int getDisplayWidth(AbyssMagicTableDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.category.projectk.abyss_magic_table");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ProjectKBlocks.ABYSS_MAGIC_TABLE.get());
    }

    @Override
    public List<Widget> setupDisplay(AbyssMagicTableDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x + 10, bounds.y + 18);
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createSlot(new Point(start.x, start.y)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createArrow(new Point(start.x + 24, start.y)));
        widgets.add(Widgets.createSlot(new Point(start.x + 60, start.y)).entries(display.getOutputEntries().get(0)).markOutput());

        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            ResourceLocation energyId = display.getEnergyId();
            long amount = display.getEnergyAmount();
            int barX = bounds.x + 10;
            int barY = bounds.y + 48;
            EnergyBarRenderer.renderFluidBarHorizontal(graphics, energyId, amount, Math.max(1L, amount), barX, barY, 80, 8);
            Component name = AbyssEnergyItem.resolveEnergyName(energyId);
            Component label = Component.translatable("energy.projectk.abyss_energy_format", name)
                    .append(Component.literal(" "))
                    .append(Component.literal(Long.toString(amount)));
            graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, label, barX, bounds.y + 6, 0xFF555555, false);
        }));

        return widgets;
    }
}
