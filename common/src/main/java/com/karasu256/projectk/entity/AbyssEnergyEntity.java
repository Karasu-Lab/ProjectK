package com.karasu256.projectk.entity;

import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.IProjectKEnergy;
import com.karasu256.projectk.particle.AbyssParticleOptions;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AbyssEnergyEntity extends Entity {
    private static final EntityDataAccessor<Long> ENERGY = SynchedEntityData.defineId(AbyssEnergyEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<String> ENERGY_ID = SynchedEntityData.defineId(AbyssEnergyEntity.class, EntityDataSerializers.STRING);
    private BlockPos targetGenerator = null;

    public AbyssEnergyEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0;
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return getName();
    }

    @Override
    @NotNull
    public Component getName() {
        var registrar = RegistrarManager.get(KarasunikiLib.MOD_ID).get(KarasunikiRegistries.ENERGY_REGISTRY_KEY);
        IEnergy energy = registrar.get(getEnergyId());
        if (energy instanceof IProjectKEnergy pkEnergy) {
            return pkEnergy.getFormatted();
        }
        return super.getName();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ENERGY, 0L);
        builder.define(ENERGY_ID, Id.id("abyss_energy").toString());
    }

    public long getEnergy() {
        return entityData.get(ENERGY);
    }

    public void setEnergy(long amount) {
        entityData.set(ENERGY, amount);
    }

    public ResourceLocation getEnergyId() {
        return ResourceLocation.parse(entityData.get(ENERGY_ID));
    }

    public void setEnergyId(ResourceLocation id) {
        entityData.set(ENERGY_ID, id.toString());
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
        if (be instanceof ICableInputable energyAcceptor && be instanceof ICapacity capacityProvider) {
            if (capacityProvider.getCapacity() <= 0) {
                return false;
            }
            return energyAcceptor.insert(getEnergyId(), 1, true) > 0;
        }
        return false;
    }

    private void findTargetGenerator() {
        BlockPos.betweenClosedStream(blockPosition().offset(-10, -10, -10), blockPosition().offset(10, 10, 10)).filter(this::isValidTarget).findFirst().ifPresent(pos -> targetGenerator = pos.immutable());
    }

    private void insertEnergy() {
        BlockEntity be = level().getBlockEntity(targetGenerator);
        if (be instanceof ICableInputable energyAcceptor) {
            Vec3 center = Vec3.atCenterOf(targetGenerator);
            Vec3 diff = position().subtract(center);
            Direction side = Direction.getNearest(diff.x, diff.y, diff.z);
            energyAcceptor.insert(getEnergyId(), getEnergy(), false, side);
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            level().addParticle(new AbyssParticleOptions(getEnergyId()), getX() + (random.nextDouble() - 0.5) * 0.3, getY() + (random.nextDouble() - 0.5) * 0.3, getZ() + (random.nextDouble() - 0.5) * 0.3, 0, 0, 0);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setEnergy(tag.getLong(EnergyKeys.ENERGY_VALUE.toString()));
        if (tag.contains(EnergyKeys.ENERGY_ID.toString())) {
            setEnergyId(ResourceLocation.parse(tag.getString(EnergyKeys.ENERGY_ID.toString())));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong(EnergyKeys.ENERGY_VALUE.toString(), getEnergy());
        tag.putString(EnergyKeys.ENERGY_ID.toString(), getEnergyId().toString());
    }
}
