/*
 * Copyright (C) 2026 Karasu256 (ProjectK owners)
 * Portions Copyright (C) 2024 KOWI2003 (Laser-Mod)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.karasu256.projectk.client.render.entity;

import com.karasu256.projectk.client.ProjectKRenderTypes;
import com.karasu256.projectk.entity.AbyssLaserEntity;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AbyssLaserEntityRenderer extends EntityRenderer<AbyssLaserEntity> {
    public AbyssLaserEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AbyssLaserEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Direction facing = entity.getFacing();
        Vec3 origin = entity.getPosition(partialTicks);
        Vec3 target = entity.getTarget();
        float distance = (float) origin.distanceTo(target);

        if (distance < 0.0001)
            return;

        ResourceLocation energyId = entity.getEnergyId();
        var attributes = ProjectKFluids.getAttributes(energyId);
        if (attributes == null)
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(attributes.getSourceTexture());

        VertexConsumer consumer = buffer.getBuffer(ProjectKRenderTypes.ABYSS_LASER.apply(sprite.atlasLocation()));

        poseStack.pushPose();

        poseStack.mulPose(Axis.XP.rotationDegrees(facing.getStepY() * 90.0F - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((Math.abs(facing.getStepY()) - 1.0F) * (facing.toYRot() + 180.0F)));

        PoseStack.Pose last = poseStack.last();

        float size = 0.05f;
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        renderFace(consumer, last, -size, size, 0, 0, 0, distance, u0, u1, v0, v1, packedLight);
        renderFace(consumer, last, 0, 0, -size, size, 0, distance, u0, u1, v0, v1, packedLight);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderFace(VertexConsumer consumer, PoseStack.Pose last, float x0, float x1, float z0, float z1, float y0, float y1, float u0, float u1, float v0, float v1, int light) {
        addVertex(consumer, last, x0, y0, z0, u0, v0, light);
        addVertex(consumer, last, x1, y0, z1, u1, v0, light);
        addVertex(consumer, last, x1, y1, z1, u1, v1, light);
        addVertex(consumer, last, x0, y1, z0, u0, v1, light);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose last, float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(last.pose(), x, y, z).setColor(255, 255, 255, 255).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(last, 0, 0, 1);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull AbyssLaserEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
