package com.karasu256.projectk.client.render.block;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.entity.AbyssEnergyCableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

import java.util.function.Function;

public class AbyssEnergyCableRenderer implements BlockEntityRenderer<AbyssEnergyCableBlockEntity> {

    private static final ResourceLocation INPUT_TEX = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
            "block/multipart/abyss_energy_cable_input");
    private static final ResourceLocation OUTPUT_TEX = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
            "block/multipart/abyss_energy_cable_output");

    public AbyssEnergyCableRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbyssEnergyCableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite inputSprite = atlas.apply(INPUT_TEX);
        TextureAtlasSprite outputSprite = atlas.apply(OUTPUT_TEX);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        for (Direction dir : Direction.values()) {
            AbyssEnergyCable.ConnectionMode mode = blockEntity.getModeForSide(dir);
            if (mode == AbyssEnergyCable.ConnectionMode.INPUT) {
                renderIndicator(poseStack, vertexConsumer, dir, inputSprite, packedLight, packedOverlay, mode);
            } else if (mode == AbyssEnergyCable.ConnectionMode.OUTPUT) {
                renderIndicator(poseStack, vertexConsumer, dir, outputSprite, packedLight, packedOverlay, mode);
            }
        }
    }

    private void renderIndicator(PoseStack poseStack, VertexConsumer vertexConsumer, Direction dir, TextureAtlasSprite sprite, int light, int overlay, AbyssEnergyCable.ConnectionMode mode) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.5D, 0.5D);

        switch (dir) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            case NORTH -> {
            }
        }

        poseStack.translate(-0.5D, -0.5D, -0.5D);

        float p = 0.002f;
        if (mode == AbyssEnergyCable.ConnectionMode.INPUT) {
            drawBox(poseStack, vertexConsumer, 4 / 16f - p, 4 / 16f - p, 0 / 16f - p, 12 / 16f + p, 12 / 16f + p,
                    2 / 16f + p, sprite, light, overlay);
            drawBox(poseStack, vertexConsumer, 6 / 16f - p, 6 / 16f - p, 2 / 16f - p, 10 / 16f + p, 10 / 16f + p,
                    4 / 16f + p, sprite, light, overlay);
            drawBox(poseStack, vertexConsumer, 5 / 16f - p, 5 / 16f - p, 4 / 16f - p, 11 / 16f + p, 11 / 16f + p,
                    5 / 16f + p, sprite, light, overlay);
        } else {
            drawBox(poseStack, vertexConsumer, 7 / 16f - p, 7 / 16f - p, 0 / 16f - p, 9 / 16f + p, 9 / 16f + p,
                    2 / 16f + p, sprite, light, overlay);
            drawBox(poseStack, vertexConsumer, 6 / 16f - p, 6 / 16f - p, 2 / 16f - p, 10 / 16f + p, 10 / 16f + p,
                    3 / 16f + p, sprite, light, overlay);
            drawBox(poseStack, vertexConsumer, 5 / 16f - p, 5 / 16f - p, 3 / 16f - p, 11 / 16f + p, 11 / 16f + p,
                    5 / 16f + p, sprite, light, overlay);
        }

        poseStack.popPose();
    }

    private void drawBox(PoseStack poseStack, VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, TextureAtlasSprite sprite, int light, int overlay) {
        Matrix4f pose = poseStack.last().pose();

        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();

        consumer.addVertex(pose, x1, y2, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, -1);
        consumer.addVertex(pose, x2, y2, z1).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, -1);
        consumer.addVertex(pose, x2, y1, z1).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, -1);
        consumer.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, -1);

        consumer.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, 1);
        consumer.addVertex(pose, x1, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, 1);
        consumer.addVertex(pose, x1, y1, z2).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, 1);
        consumer.addVertex(pose, x2, y1, z2).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, 0, 1);

        consumer.addVertex(pose, x2, y2, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay)
                .setLight(light).setNormal(1, 0, 0);
        consumer.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay)
                .setLight(light).setNormal(1, 0, 0);
        consumer.addVertex(pose, x2, y1, z2).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay)
                .setLight(light).setNormal(1, 0, 0);
        consumer.addVertex(pose, x2, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay)
                .setLight(light).setNormal(1, 0, 0);

        consumer.addVertex(pose, x1, y2, z2).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay)
                .setLight(light).setNormal(-1, 0, 0);
        consumer.addVertex(pose, x1, y2, z1).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay)
                .setLight(light).setNormal(-1, 0, 0);
        consumer.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay)
                .setLight(light).setNormal(-1, 0, 0);
        consumer.addVertex(pose, x1, y1, z2).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay)
                .setLight(light).setNormal(-1, 0, 0);

        consumer.addVertex(pose, x1, y2, z2).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, 1, 0);
        consumer.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, 1, 0);
        consumer.addVertex(pose, x2, y2, z1).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, 1, 0);
        consumer.addVertex(pose, x1, y2, z1).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, 1, 0);

        consumer.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, -1, 0);
        consumer.addVertex(pose, x2, y1, z1).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(overlay)
                .setLight(light).setNormal(0, -1, 0);
        consumer.addVertex(pose, x2, y1, z2).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, -1, 0);
        consumer.addVertex(pose, x1, y1, z2).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(overlay)
                .setLight(light).setNormal(0, -1, 0);
    }
}
