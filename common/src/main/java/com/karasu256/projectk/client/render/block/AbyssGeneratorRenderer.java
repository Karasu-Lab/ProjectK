package com.karasu256.projectk.client.render.block;

import com.karasu256.projectk.block.entity.AbyssGeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class AbyssGeneratorRenderer implements BlockEntityRenderer<AbyssGeneratorBlockEntity> {
    public AbyssGeneratorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbyssGeneratorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.25, 0.5);

        long time = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
        float rotation = (time + partialTick) * blockEntity.getRotationAnimSpeed().getRotationSpeed() * 5.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        poseStack.scale(0.75f, 0.75f, 0.75f);

        HoldableBlockEntityRenderer.renderItem(blockEntity, poseStack, bufferSource, 15728880, packedOverlay);

        poseStack.popPose();
    }
}
