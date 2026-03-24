package com.karasu256.projectk.client.render.entity;

import com.karasu256.projectk.client.ProjectKRenderTypes;
import com.karasu256.projectk.entity.AbyssEnergyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class AbyssEnergyEntityRenderer extends EntityRenderer<AbyssEnergyEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/particles/sculk_charge_pop_0.png");

    public AbyssEnergyEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AbyssEnergyEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        
        VertexConsumer vertexConsumer = buffer.getBuffer(ProjectKRenderTypes.ABYSS_ENERGY.apply(TEXTURE));
        Matrix4f matrix4f = poseStack.last().pose();

        float size = 0.25f;

        renderVertex(vertexConsumer, matrix4f, poseStack, -size, -size, 0.0f, 0, 1, packedLight);
        renderVertex(vertexConsumer, matrix4f, poseStack, size, -size, 0.0f, 1, 1, packedLight);
        renderVertex(vertexConsumer, matrix4f, poseStack, size, size, 0.0f, 1, 0, packedLight);
        renderVertex(vertexConsumer, matrix4f, poseStack, -size, size, 0.0f, 0, 0, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderVertex(VertexConsumer consumer, Matrix4f mat, PoseStack pose, float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(mat, x, y, z)
                .setColor(200, 50, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose.last(), 0, 1, 0);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(AbyssEnergyEntity entity) {
        return TEXTURE;
    }
}
