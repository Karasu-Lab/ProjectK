package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssCoreBlockEntity extends BlockEntity implements GeoAnimatableBlockEntity {
    public AbyssCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.ABYSS_CORE.get(), pos, state);
    }

    @Override
    public ResourceLocation getModelResource() {
        return Id.id("geo/abyss_core.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource() {
        return Id.id("textures/block/abyss_core.png");
    }

    @Override
    public ResourceLocation getAnimationResource() {
        return Id.id("animations/abyss_core.animation.json");
    }
}
