package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;

public final class EnergyBarRenderer {
    private static final int SPRITE_SIZE = 16;

    private EnergyBarRenderer() {
    }

    public static void renderFluidBar(GuiGraphics graphics, ResourceLocation energyId, long amount, long capacity, int x, int y, int width, int height) {
        if (energyId == null || capacity <= 0) {
            return;
        }
        int fill = (int) Math.min((amount * (long) height) / capacity, height);
        if (fill <= 0) {
            return;
        }
        ResourceLocation spriteId = ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(), "block/fluid_" + energyId.getPath() + "_still");
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(spriteId);
        int clipY = y + height - fill;

        graphics.enableScissor(x, clipY, x + width, y + height);
        for (int drawY = y + height - SPRITE_SIZE; drawY > clipY - SPRITE_SIZE; drawY -= SPRITE_SIZE) {
            graphics.blit(x, drawY, 0, width, SPRITE_SIZE, sprite);
        }
        graphics.disableScissor();
    }

    public static List<FormattedCharSequence> toolTip(ResourceLocation energyId, long amount, long capacity) {
        if (energyId == null) {
            return List.of();
        }
        return ProjectKEnergies.getDefinition(energyId).map(definition -> {
            Component name = Component.translatable("energy.projectk." + definition.idPath());
            Component formatted = Component.translatable("energy.projectk.abyss_energy_format", name);
            Component energyLine = Component.translatable("tooltip.projectk.wthit.energy", amount, capacity);
            return List.of(formatted.getVisualOrderText(), energyLine.getVisualOrderText());
        }).orElseGet(() -> List.of(Component.literal(energyId.toString()).getVisualOrderText()));
    }
}
