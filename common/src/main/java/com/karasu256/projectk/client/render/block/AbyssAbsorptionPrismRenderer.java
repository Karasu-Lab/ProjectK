package com.karasu256.projectk.client.render.block;

import com.karasu256.projectk.block.entity.AbyssAbsorptionPrismBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.item.ProjectKItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AbyssAbsorptionPrismRenderer implements BlockEntityRenderer<AbyssAbsorptionPrismBlockEntity> {
    public AbyssAbsorptionPrismRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbyssAbsorptionPrismBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        List<AbyssEnergyData> energies = blockEntity.getEnergies();
        if (energies.isEmpty()) {
            return;
        }

        ResourceLocation energyId = energies.get(0).energyId();
        ItemStack displayStack = new ItemStack(ProjectKItems.ABYSS_INGOT.get());
        AbyssEnergyData.applyToStack(displayStack, energyId, 1L);

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);

        float time = (blockEntity.getLevel().getGameTime() + partialTick) * 0.05f;
        poseStack.mulPose(Axis.YP.rotation(time));
        poseStack.scale(0.6f, 0.6f, 0.6f);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(displayStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack,
                bufferSource, blockEntity.getLevel(), 0);

        poseStack.popPose();
    }
}
