package com.karasu256.projectk.client.render.block;

import com.karasu256.projectk.block.entity.AbyssGeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AbyssGeneratorRenderer implements BlockEntityRenderer<AbyssGeneratorBlockEntity> {
    public AbyssGeneratorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbyssGeneratorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack heldItem = blockEntity.getHeldItem();
        if (heldItem.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(heldItem, blockEntity.getLevel(), null, 0);

        poseStack.pushPose();
        // Translate to center of the block and slightly above
        poseStack.translate(0.5, 1.25, 0.5);
        
        // Calculate rotation
        long time = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
        float rotation = (time + partialTick) * blockEntity.getRotationSpeed() * 5.0f; // Multiplied by 5 for visible speed
        
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        
        poseStack.scale(0.5f, 0.5f, 0.5f);

        itemRenderer.renderStatic(heldItem, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
        
        poseStack.popPose();
    }
}
