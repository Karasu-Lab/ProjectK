package com.karasu256.projectk.block.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoBlockEntity;

public interface GeoAnimatableBlockEntity extends GeoBlockEntity {
    ResourceLocation getModelResource();

    ResourceLocation getTextureResource();

    ResourceLocation getAnimationResource();
}
