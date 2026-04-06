package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.screen.EnergyBarRenderer;
import com.karasu256.projectk.data.AbyssEnergyData;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AbyssSynthesizerCategory implements DisplayCategory<AbyssSynthesizerDisplay> {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 140;

    @Override
    public CategoryIdentifier<? extends AbyssSynthesizerDisplay> getCategoryIdentifier() {
        return AbyssSynthesizerDisplay.ID;
    }

    @Override
    public int getDisplayWidth(AbyssSynthesizerDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.category.projectk.abyss_synthesizer");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ProjectKBlocks.ABYSS_SYNTHESIZER.get());
    }

    @Override
    public List<Widget> setupDisplay(AbyssSynthesizerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + 65;

        widgets.add(Widgets.createResultSlotBackground(new Point(centerX - 9, centerY - 9)));
        widgets.add(Widgets.createSlot(new Point(centerX - 8, centerY - 8)).entries(display.getOutputEntries().get(0))
                .markOutput().disableBackground());

        float radius = 38.0f;
        List<EntryIngredient> inputs = display.getInputEntries();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(-90 + 60 * i);
            int posX = centerX + (int) Math.round(Math.cos(angle) * (double) radius);
            int posY = centerY + (int) Math.round(Math.sin(angle) * (double) radius);

            widgets.add(Widgets.createSlotBackground(new Point(posX - 9, posY - 9)));

            if (i < inputs.size()) {
                widgets.add(Widgets.createSlot(new Point(posX - 8, posY - 8)).entries(inputs.get(i)).markInput()
                        .disableBackground());
            } else {
                widgets.add(Widgets.createSlot(new Point(posX - 8, posY - 8)).markInput().disableBackground());
            }
        }

        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            List<AbyssEnergyData> energies = display.getEnergies();
            long capacity = energies.stream().mapToLong(AbyssEnergyData::amountOrZero).sum();
            if (capacity > 0) {
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 100);
                EnergyBarRenderer.renderDonut(graphics, (float) centerX, (float) centerY, energies, capacity, 56.0f,
                        12.0f, -90.0f);
                graphics.pose().popPose();
            }
        }));

        widgets.add(Widgets.createTooltip(mouse -> {
            List<AbyssEnergyData> energies = display.getEnergies();
            long capacity = energies.stream().mapToLong(AbyssEnergyData::amountOrZero).sum();
            return EnergyBarRenderer.getDonutTooltipComponents(mouse.x, mouse.y, (float) centerX, (float) centerY,
                            energies, capacity, capacity, new EnergyBarRenderer.DonutRadius(56.0f - 6.0f, 56.0f + 6.0f), -90.0f)
                    .map(Tooltip::create).orElse(null);
        }));

        return widgets;
    }
}
