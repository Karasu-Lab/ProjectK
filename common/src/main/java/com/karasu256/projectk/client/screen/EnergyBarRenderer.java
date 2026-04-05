package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Optional;

public final class EnergyBarRenderer {
    private static final int SPRITE_SIZE = 16;
    private static final float ARC_STEP = 2.0f;

    private EnergyBarRenderer() {
    }

    public record DonutRadius(float inner, float outer) {
    }

    public static void renderDonut(GuiGraphics graphics, float cx, float cy, List<AbyssEnergyData> energies, long visualCapacity, DonutRadius radius, float startAngle) {
        if (visualCapacity <= 0 || energies.isEmpty())
            return;

        float innerRadius = radius.inner();
        float outerRadius = radius.outer();

        float currentAngle = startAngle;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        Matrix4f pose = graphics.pose().last().pose();

        for (AbyssEnergyData data : energies) {
            long amount = data.amount();
            if (amount <= 0)
                continue;

            float sweepDeg = ((float) amount / visualCapacity) * 360.0f;
            ResourceLocation energyId = data.energyId();
            
            ResourceLocation spriteId = ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(),
                    "block/fluid_" + energyId.getPath() + "_still");
            
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
                    .getSprite(spriteId);

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

            float u0 = sprite.getU0();
            float v0 = sprite.getV0();
            float u1 = sprite.getU1();
            float v1 = sprite.getV1();

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            float diameter = outerRadius * 2.0f;

            for (float a = 0; a < sweepDeg; a += ARC_STEP) {
                float angle1 = currentAngle + a;
                float angle2 = currentAngle + Math.min(a + ARC_STEP, sweepDeg);

                float rad1 = (float) Math.toRadians(angle1);
                float rad2 = (float) Math.toRadians(angle2);

                float cos1 = (float) Math.cos(rad1);
                float sin1 = (float) Math.sin(rad1);
                float cos2 = (float) Math.cos(rad2);
                float sin2 = (float) Math.sin(rad2);

                float ix1 = cos1 * innerRadius;
                float iy1 = sin1 * innerRadius;
                float ix2 = cos2 * innerRadius;
                float iy2 = sin2 * innerRadius;
                float ox2 = cos2 * outerRadius;
                float oy2 = sin2 * outerRadius;
                float ox1 = cos1 * outerRadius;
                float oy1 = sin1 * outerRadius;

                float tu1 = u0 + (ix1 + outerRadius) / diameter * (u1 - u0);
                float tv1 = v0 + (iy1 + outerRadius) / diameter * (v1 - v0);
                float tu2 = u0 + (ix2 + outerRadius) / diameter * (u1 - u0);
                float tv2 = v0 + (iy2 + outerRadius) / diameter * (v1 - v0);
                float tu3 = u0 + (ox2 + outerRadius) / diameter * (u1 - u0);
                float tv3 = v0 + (oy2 + outerRadius) / diameter * (v1 - v0);
                float tu4 = u0 + (ox1 + outerRadius) / diameter * (u1 - u0);
                float tv4 = v0 + (oy1 + outerRadius) / diameter * (v1 - v0);

                buffer.addVertex(pose, cx + ix1, cy + iy1, 0).setUv(tu1, tv1);
                buffer.addVertex(pose, cx + ix2, cy + iy2, 0).setUv(tu2, tv2);
                buffer.addVertex(pose, cx + ox2, cy + oy2, 0).setUv(tu3, tv3);
                buffer.addVertex(pose, cx + ox1, cy + oy1, 0).setUv(tu4, tv4);
            }

            MeshData meshData = buffer.buildOrThrow();
            BufferUploader.drawWithShader(meshData);

            currentAngle += sweepDeg;
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    public static void renderDonut(GuiGraphics graphics, float cx, float cy, List<AbyssEnergyData> energies, long visualCapacity, float centerRadius, float thickness, float startAngle) {
        float halfThickness = thickness / 2.0f;
        renderDonut(graphics, cx, cy, energies, visualCapacity,
                new DonutRadius(centerRadius - halfThickness, centerRadius + halfThickness), startAngle);
    }

    public static void renderDonutTooltip(GuiGraphics graphics, Font font, int mouseX, int mouseY, float cx, float cy, List<AbyssEnergyData> energies, long visualCapacity, long tooltipCapacity, float centerRadius, float thickness, float startAngle) {
        getDonutTooltipComponents(mouseX, mouseY, cx, cy, energies, visualCapacity, tooltipCapacity,
                new DonutRadius(centerRadius - thickness / 2.0f, centerRadius + thickness / 2.0f), startAngle)
                .ifPresent(tooltip -> {
                    graphics.renderTooltip(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX,
                            mouseY);
                });
    }

    public static Optional<List<Component>> getDonutTooltipComponents(int mouseX, int mouseY, float cx, float cy, List<AbyssEnergyData> energies, long visualCapacity, long tooltipCapacity, DonutRadius radius, float startAngle) {
        float dx = mouseX - cx;
        float dy = mouseY - cy;
        float distSq = dx * dx + dy * dy;

        float inner = radius.inner();
        float outer = radius.outer();
        if (distSq < inner * inner || distSq > outer * outer) {
            return Optional.empty();
        }

        if (visualCapacity <= 0)
            return Optional.empty();

        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (angle < startAngle)
            angle += 360;

        float currentAngle = startAngle;
        for (AbyssEnergyData data : energies) {
            long amount = data.amount();
            if (amount <= 0)
                continue;

            float sweepDeg = ((float) amount / visualCapacity) * 360.0f;
            float startNorm = currentAngle;
            if (startNorm < startAngle)
                startNorm += 360;
            float endNorm = startNorm + sweepDeg;

            float testAngle = angle;
            if (endNorm > (startAngle + 360) && testAngle < startNorm) {
                testAngle += 360;
            }

            if (testAngle >= startNorm && testAngle <= endNorm) {
                return Optional.of(toolTipComponents(data.energyId(), amount, tooltipCapacity));
            }
            currentAngle += sweepDeg;
        }

        return Optional.empty();
    }

    public static void renderFluidBar(GuiGraphics graphics, ResourceLocation energyId, long amount, long capacity, int x, int y, int width, int height) {
        if (energyId == null || capacity <= 0) {
            return;
        }
        int fill = (int) Math.min((amount * (long) height) / capacity, height);
        if (fill <= 0) {
            return;
        }
        ResourceLocation spriteId = ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(),
                "block/fluid_" + energyId.getPath() + "_still");
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
                .getSprite(spriteId);
        int clipY = y + height - fill;

        graphics.enableScissor(x, clipY, x + width, y + height);
        for (int drawY = y + height - SPRITE_SIZE; drawY > clipY - SPRITE_SIZE; drawY -= SPRITE_SIZE) {
            graphics.blit(x, drawY, 0, width, SPRITE_SIZE, sprite);
        }
        graphics.disableScissor();
    }

    public static void renderFluidBarHorizontal(GuiGraphics graphics, ResourceLocation energyId, long amount, long capacity, int x, int y, int width, int height) {
        if (energyId == null || capacity <= 0) {
            return;
        }
        int fill = (int) Math.min((amount * (long) width) / capacity, width);
        if (fill <= 0) {
            return;
        }
        ResourceLocation spriteId = ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(),
                "block/fluid_" + energyId.getPath() + "_still");
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
                .getSprite(spriteId);
        int clipX = x + fill;

        graphics.enableScissor(x, y, clipX, y + height);
        for (int drawX = x; drawX < x + width; drawX += SPRITE_SIZE) {
            graphics.blit(drawX, y, 0, SPRITE_SIZE, height, sprite);
        }
        graphics.disableScissor();
    }

    public static List<Component> toolTipComponents(ResourceLocation energyId, long amount, long capacity) {
        if (energyId == null) {
            return List.of();
        }
        return ProjectKEnergies.getDefinition(energyId).map(definition -> {
            Component formatted = Component.translatable("energy.projectk.abyss_energy_format", definition.getDisplayName());
            Component energyLine = Component.translatable("tooltip.projectk.wthit.energy", amount, capacity);
            return List.of(formatted, energyLine);
        }).orElseGet(() -> List.of(Component.literal(energyId.toString())));
    }

    public static List<FormattedCharSequence> toolTip(ResourceLocation energyId, long amount, long capacity) {
        return toolTipComponents(energyId, amount, capacity).stream().map(Component::getVisualOrderText).toList();
    }
}
