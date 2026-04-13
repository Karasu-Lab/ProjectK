package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.entity.AbyssPortalEnergyEntity;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.utils.Id;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Locale;
import java.util.Optional;

public class AbyssCoreBlockEntity extends AbstractAbyssMachineBlockEntity implements GeoAnimatableBlockEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AbyssCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_CORE.get(), pos, state, ProjectKMachineCapacities.ABYSS_CORE);
    }

    public void tick() {
        Optional.ofNullable(level).filter(l -> !l.isClientSide).filter(l -> l.getGameTime() % 2 < 1).ifPresent(
                l -> Optional.ofNullable(getAbyssEnergyId()).filter(energyId -> getEnergyAmount() >= 100)
                        .ifPresent(id -> Optional.ofNullable(findNearbyPortal(id)).ifPresent(targetPos -> {
                            AbyssPortalEnergyEntity entity = new AbyssPortalEnergyEntity(
                                    ProjectKEntities.ABYSS_PORTAL_ENERGY_ENTITY.get(), l);
                            entity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                                    worldPosition.getZ() + 0.5);
                            entity.setEnergyId(id);
                            entity.setEnergy(100);
                            entity.setTargetGenerator(targetPos);
                            l.addFreshEntity(entity);
                            extract(id, 100, false);
                        })));
    }

    private BlockPos findNearbyPortal(ResourceLocation energyId) {
        for (int x = -10; x <= 10; x++) {
            for (int y = -10; y <= 10; y++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos p = worldPosition.offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(p);
                    if (be instanceof AbyssPortalBlockEntity portal) {
                        if (portal.acceptEnergyFromParticle(energyId, 1, true, null) > 0) {
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ResourceLocation getModelResource() {
        return Id.id("geo/abyss_core.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource() {
        return Optional.ofNullable(getAbyssEnergyId()).flatMap(ProjectKEnergies::getDefinition).map(def -> {
            if (def.enName().toLowerCase(Locale.ROOT).equals("abyss")) {
                return Id.id("textures/block/abyss_core.png");
            }
            return Id.id("textures/block/abyss_core_" + def.enName().toLowerCase(Locale.ROOT) + ".png");
        }).orElse(Id.id("textures/block/abyss_core.png"));
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
