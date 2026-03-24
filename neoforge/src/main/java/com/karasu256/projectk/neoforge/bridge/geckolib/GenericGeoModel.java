package com.karasu256.projectk.neoforge.bridge.geckolib;

import com.karasu256.projectk.block.entity.GeoAnimatableBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class GenericGeoModel<T extends BlockEntity & GeoAnimatableBlockEntity & GeoAnimatable> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return animatable.getModelResource();
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return animatable.getTextureResource();
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animatable.getAnimationResource();
    }
}
