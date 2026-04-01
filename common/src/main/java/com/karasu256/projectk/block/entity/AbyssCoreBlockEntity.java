package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

public class AbyssCoreBlockEntity extends BlockEntity implements GeoAnimatableBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public AbyssCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_CORE.get(), pos, state);
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
