package com.karasu256.projectk.client.render;

import net.minecraft.resources.ResourceLocation;

public interface GeoModelProvider {
    ResourceLocation getModelResource();
    ResourceLocation getTextureResource();
    ResourceLocation getAnimationResource();
}
