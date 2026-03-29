package com.karasu256.projectk.client.render.block;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.entity.AbyssGeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class AbyssGeneratorRenderer implements BlockEntityRenderer<AbyssGeneratorBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;

    public AbyssGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AbyssGeneratorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        ResourceLocation energyId = blockEntity.getEnergyType().getId();
        ModelResourceLocation modelId = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(energyId.getNamespace(), "block/abyss_generator/" + energyId.getPath()), "standalone");

        BakedModel bakedModel = this.blockRenderDispatcher.getBlockModelShaper().getModelManager().getModel(modelId);
        if (bakedModel != this.blockRenderDispatcher.getBlockModelShaper().getModelManager().getMissingModel()) {
            this.blockRenderDispatcher.getModelRenderer().renderModel(
                    poseStack.last(),
                    bufferSource.getBuffer(RenderType.cutout()),
                    blockEntity.getBlockState(),
                    bakedModel,
                    1.0f, 1.0f, 1.0f,
                    packedLight, packedOverlay
            );
        }
        poseStack.popPose();

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
