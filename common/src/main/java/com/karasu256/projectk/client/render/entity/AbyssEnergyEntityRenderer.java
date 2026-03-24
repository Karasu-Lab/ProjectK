package com.karasu256.projectk.client.render.entity;

import com.karasu256.projectk.client.ProjectKRenderTypes;
import com.karasu256.projectk.energy.AbyssEnergyColor;
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

import java.awt.*;

public class AbyssEnergyEntityRenderer extends EntityRenderer<AbyssEnergyEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/particle/sculk_charge_pop_0.png");

    public AbyssEnergyEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AbyssEnergyEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        poseStack.translate(0.0, 0.2, 0.0);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        VertexConsumer vertexConsumer = buffer.getBuffer(ProjectKRenderTypes.ABYSS_ENERGY.apply(TEXTURE));
        Matrix4f matrix4f = poseStack.last().pose();

        float time = (entity.tickCount + partialTicks) * 0.2f;
        float size = 0.275f + (float) Math.sin(time) * 0.025f;

        Color color = AbyssEnergyColor.getColor(entity.getEnergy());

        renderVertex(vertexConsumer, matrix4f, poseStack, -size, -size, 0.0f, 0, 1, color, packedLight);
        renderVertex(vertexConsumer, matrix4f, poseStack, size, -size, 0.0f, 1, 1, color, packedLight);
        renderVertex(vertexConsumer, matrix4f, poseStack, size, size, 0.0f, 1, 0, color, packedLight);
        renderVertex(vertexConsumer, matrix4f, poseStack, -size, size, 0.0f, 0, 0, color, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderVertex(VertexConsumer consumer, Matrix4f mat, PoseStack pose, float x, float y, float z, float u, float v, Color color, int light) {
        consumer.addVertex(mat, x, y, z)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), 255)
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
