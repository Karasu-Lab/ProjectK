package com.karasu256.projectk.fabric.bridge.geckolib;

import com.karasu256.projectk.block.entity.GeoAnimatableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GenericGeoBlockRenderer<T extends BlockEntity & GeoAnimatableBlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {
    public GenericGeoBlockRenderer() {
        super(new GenericGeoModel<>());
    }
}
