package com.karasu256.projectk.client.render.block.geckolib;

import com.karasu256.projectk.block.entity.GeoAnimatableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AbyssGeoBlockRenderer<T extends BlockEntity & GeoAnimatableBlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {
    public AbyssGeoBlockRenderer() {
        super(new GenericGeoModel<>());
    }
}
