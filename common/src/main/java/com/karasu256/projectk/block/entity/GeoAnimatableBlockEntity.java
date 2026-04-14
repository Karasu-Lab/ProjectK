package com.karasu256.projectk.block.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.util.RenderUtil;

public interface GeoAnimatableBlockEntity extends GeoBlockEntity {
    @Override
    default double getTick(Object blockEntity) {
        if (blockEntity instanceof BlockEntity be && be.getLevel() != null) {
            return be.getLevel().getGameTime();
        }
        return RenderUtil.getCurrentTick();
    }

    ResourceLocation getModelResource();

    ResourceLocation getTextureResource();

    ResourceLocation getAnimationResource();
}
