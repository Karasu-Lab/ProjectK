package com.karasu256.projectk.client.render.entity;

import com.karasu256.projectk.client.ProjectKRenderTypes;
import com.karasu256.projectk.energy.AbyssEnergyUtils;
import com.karasu256.projectk.entity.AbyssBurstEntity;
import com.karasu256.projectk.particle.AbyssBurstParticle;
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

public class AbyssBurstEntityRenderer extends EntityRenderer<AbyssBurstEntity> {
    public AbyssBurstEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AbyssBurstEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0.0, 0.2, 0.0);
        poseStack.scale(0.8f, 0.8f, 0.8f);

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
 
        VertexConsumer vertexConsumer = buffer.getBuffer(
                ProjectKRenderTypes.ABYSS_BURST.apply(AbyssBurstParticle.TEXTURE));
        PoseStack.Pose last = poseStack.last();
 
        float size = 0.4f;
        int colorInt = AbyssEnergyUtils.getEnergyColor(entity.getEnergyId());
        Color color = new Color(colorInt);
 
        renderVertex(vertexConsumer, last, -size, -size, 0.0f, 0, 1, color, packedLight);
        renderVertex(vertexConsumer, last, size, -size, 0.0f, 1, 1, color, packedLight);
        renderVertex(vertexConsumer, last, size, size, 0.0f, 1, 0, color, packedLight);
        renderVertex(vertexConsumer, last, -size, size, 0.0f, 0, 0, color, packedLight);


        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderVertex(VertexConsumer consumer, PoseStack.Pose last, float x, float y, float z, float u, float v, Color color, int light) {
        consumer.addVertex(last.pose(), x, y, z).setColor(color.getRed(), color.getGreen(), color.getBlue(), 255).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(last, 0, 1, 0);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull AbyssBurstEntity entity) {
        return AbyssBurstParticle.TEXTURE;
    }
}
