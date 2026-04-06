package com.karasu256.projectk.entity;

import com.karasu256.projectk.energy.EnergyKeys;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AbyssLaserEntity extends Entity {
    private static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(AbyssLaserEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(AbyssLaserEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(AbyssLaserEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> ENERGY_ID = SynchedEntityData.defineId(AbyssLaserEntity.class,
            EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(AbyssLaserEntity.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> ENERGY_AMOUNT = SynchedEntityData.defineId(AbyssLaserEntity.class,
            EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(AbyssLaserEntity.class,
            EntityDataSerializers.DIRECTION);

    public AbyssLaserEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TARGET_X, 0f);
        builder.define(TARGET_Y, 0f);
        builder.define(TARGET_Z, 0f);
        builder.define(ENERGY_ID, "projectk:abyss_energy");
        builder.define(ENERGY_AMOUNT, 0L);
        builder.define(LIFETIME, 20);
        builder.define(FACING, Direction.NORTH);
    }

    private void updateAABB() {
        Vec3 end = getTarget();
        this.setBoundingBox(new AABB(position(), end).inflate(0.1));
    }

    public Vec3 getTarget() {
        return new Vec3(this.entityData.get(TARGET_X), this.entityData.get(TARGET_Y), this.entityData.get(TARGET_Z));
    }

    public void setTarget(Vec3 target) {
        this.entityData.set(TARGET_X, (float) target.x);
        this.entityData.set(TARGET_Y, (float) target.y);
        this.entityData.set(TARGET_Z, (float) target.z);
        this.updateAABB();
    }

    public ResourceLocation getEnergyId() {
        return ResourceLocation.parse(this.entityData.get(ENERGY_ID));
    }

    public void setEnergyId(ResourceLocation id) {
        this.entityData.set(ENERGY_ID, id.toString());
    }

    public long getEnergyAmount() {
        return this.entityData.get(ENERGY_AMOUNT);
    }

    public void setEnergyAmount(long amount) {
        this.entityData.set(ENERGY_AMOUNT, amount);
    }

    public Direction getFacing() {
        return this.entityData.get(FACING);
    }

    public void setFacing(Direction facing) {
        this.entityData.set(FACING, facing);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int lifetime) {
        this.entityData.set(LIFETIME, lifetime);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.updateAABB();
        }
        if (this.tickCount > getLifetime()) {
            this.discard();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        if (compound.contains(EnergyKeys.ENERGY_ID.toString())) {
            setEnergyId(ResourceLocation.parse(compound.getString(EnergyKeys.ENERGY_ID.toString())));
        }
        if (compound.contains(EnergyKeys.ENERGY_VALUE.toString())) {
            setEnergyAmount(compound.getLong(EnergyKeys.ENERGY_VALUE.toString()));
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        compound.putString(EnergyKeys.ENERGY_ID.toString(), getEnergyId().toString());
        compound.putLong(EnergyKeys.ENERGY_VALUE.toString(), getEnergyAmount());
    }
}
