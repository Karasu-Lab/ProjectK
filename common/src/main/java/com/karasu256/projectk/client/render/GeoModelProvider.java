package com.karasu256.projectk.client.render;

import net.minecraft.resources.ResourceLocation;

@Deprecated
public interface GeoModelProvider {
    ResourceLocation getModelResource();
    ResourceLocation getTextureResource();
    ResourceLocation getAnimationResource();
}
