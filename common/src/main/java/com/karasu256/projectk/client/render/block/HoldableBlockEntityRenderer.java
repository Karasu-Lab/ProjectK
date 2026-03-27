package com.karasu256.projectk.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.karasuniki.karasunikilib.api.IHeldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HoldableBlockEntityRenderer {
    public static void renderItem(BlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(blockEntity instanceof IHeldItem heldItemEntity)) {
            return;
        }

        ItemStack heldItem = heldItemEntity.getHeldItem();
        if (heldItem.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(heldItem, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
    }
}
