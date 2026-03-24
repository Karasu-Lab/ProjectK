package com.karasu256.projectk.entity;

import com.karasu256.projectk.energy.IProjectKEnergy;
import com.karasu256.projectk.registry.ParticlesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class AbyssEnergyEntity extends Entity {
    private static final EntityDataAccessor<Long> ENERGY = SynchedEntityData.defineId(AbyssEnergyEntity.class, EntityDataSerializers.LONG);
    private BlockPos targetGenerator = null;

    public AbyssEnergyEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ENERGY, 0L);
    }

    public void setEnergy(long amount) {
        entityData.set(ENERGY, amount);
    }

    public long getEnergy() {
        return entityData.get(ENERGY);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnParticles();
            move(MoverType.SELF, getDeltaMovement());
            return;
        }

        if (targetGenerator == null || !isValidTarget(targetGenerator)) {
            findTargetGenerator();
        }

        if (targetGenerator != null) {
            Vec3 targetPos = Vec3.atCenterOf(targetGenerator);
            double distSq = position().distanceToSqr(targetPos);

            if (distSq < 0.5) {
                insertEnergy();
                discard();
            } else {
                moveSmoothly(targetPos);
            }
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.9).add(0, -0.01, 0));
            move(MoverType.SELF, getDeltaMovement());
        }

        if (tickCount > 600) {
            discard();
        }
    }

    private void moveSmoothly(Vec3 targetPos) {
        Vec3 dir = targetPos.subtract(position()).normalize();
        Vec3 currentMov = getDeltaMovement();
        
        double accel = 0.02;
        double friction = 0.92;
        
        Vec3 newMov = currentMov.add(dir.scale(accel)).scale(friction);
        setDeltaMovement(newMov);
        move(MoverType.SELF, getDeltaMovement());
    }

    private boolean isValidTarget(BlockPos pos) {
        BlockEntity be = level().getBlockEntity(pos);
        return be instanceof IProjectKEnergy energyAcceptor && energyAcceptor.getAmount() < energyAcceptor.getCapacity();
    }

    private void findTargetGenerator() {
        BlockPos.betweenClosedStream(blockPosition().offset(-10, -10, -10), blockPosition().offset(10, 10, 10))
                .filter(this::isValidTarget)
                .findFirst()
                .ifPresent(pos -> targetGenerator = pos.immutable());
    }

    private void insertEnergy() {
        BlockEntity be = level().getBlockEntity(targetGenerator);
        if (be instanceof IProjectKEnergy energyAcceptor) {
            energyAcceptor.insert(getEnergy(), false);
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            level().addParticle(
                    ParticlesRegistry.ABYSS_PARTICLE.get(),
                    getX() + (random.nextDouble() - 0.5) * 0.3,
                    getY() + (random.nextDouble() - 0.5) * 0.3,
                    getZ() + (random.nextDouble() - 0.5) * 0.3,
                    0, 0, 0
            );
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setEnergy(tag.getLong("Energy"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("Energy", getEnergy());
    }
}
