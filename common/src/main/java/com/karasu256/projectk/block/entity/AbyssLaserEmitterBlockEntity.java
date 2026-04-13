package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssLaserEmitter;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssLaserEmitterTier;
import com.karasu256.projectk.data.AbyssLaserEmitterTierManager;
import com.karasu256.projectk.entity.AbyssLaserEntity;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class AbyssLaserEmitterBlockEntity extends AbstractAbyssMachineBlockEntity {
    private int cooldown = 0;

    public AbyssLaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_LASER_EMITTER.get(), pos, state,
                ProjectKMachineCapacities.ABYSS_LASER_EMITTER);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssLaserEmitterBlockEntity be) {
        if (level.isClientSide)
            return;

        AbyssLaserEmitterTier tierData = AbyssLaserEmitterTierManager.getTier(be.getTier());

        AbyssLaserEmitter.Mode mode = state.getValue(AbyssLaserEmitter.MODE);
        Direction facing = state.getValue(AbyssLaserEmitter.FACING);

        if (mode == AbyssLaserEmitter.Mode.DC) {
            be.fireLaser(level, pos, facing, tierData);
        } else {
            if (be.cooldown > 0) {
                be.cooldown--;
            } else {
                if (be.fireLaser(level, pos, facing, tierData)) {
                    be.cooldown = tierData.pulseInterval();
                }
            }
        }
    }

    private boolean fireLaser(Level level, BlockPos pos, Direction facing, AbyssLaserEmitterTier tierData) {
        ResourceLocation energyId = getAbyssEnergyId();
        if (energyId == null)
            return false;

        long energyAmount = getEnergyAmount();
        long cost = tierData.concentration() / 10;
        if (cost <= 0)
            cost = 1;

        if (energyAmount >= cost) {
            extract(energyId, cost, false);

            if (level instanceof ServerLevel) {
                Vec3 startVec = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.51));
                Vec3 endVec = startVec.add(Vec3.atLowerCornerOf(facing.getNormal()).scale(32));

                BlockHitResult result = level.clip(
                        new ClipContext(startVec, endVec, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE,
                                CollisionContext.empty()));
                Vec3 actualEndVec = result.getLocation();
                BlockPos hitPos = result.getBlockPos();
                BlockState hitState = level.getBlockState(hitPos);

                if (hitState.getBlock() instanceof com.karasu256.projectk.block.custom.ILaserEnergyReactiveBlock reactive) {
                    reactive.onLaserHit(level, hitPos, hitState, energyId, cost);
                }

                AbyssLaserEntity laser = new AbyssLaserEntity(ProjectKEntities.ABYSS_LASER_ENTITY.get(), level);
                laser.setPos(startVec);
                laser.setTarget(actualEndVec);
                laser.setEnergyId(energyId);
                laser.setEnergyAmount(cost);
                laser.setFacing(facing);
                laser.setLifetime(2);
                level.addFreshEntity(laser);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMaxTier() {
        return 3;
    }

    @Override
    public int getDefaultTier() {
        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("Cooldown", cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        cooldown = nbt.getInt("Cooldown");
    }
}
