package com.karasu256.projectk.client.render.block;

import com.karasu256.projectk.block.entity.AbyssEnergyCableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class AbyssEnergyCableBlockEntityRenderer implements BlockEntityRenderer<AbyssEnergyCableBlockEntity> {
    public AbyssEnergyCableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbyssEnergyCableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    }
}
