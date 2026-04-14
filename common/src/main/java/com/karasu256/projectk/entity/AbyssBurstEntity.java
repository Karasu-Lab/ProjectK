package com.karasu256.projectk.entity;

import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.particle.AbyssBurstParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class AbyssBurstEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Long> ENERGY_AMOUNT = SynchedEntityData.defineId(AbyssBurstEntity.class,
            EntityDataSerializers.LONG);
    private static final EntityDataAccessor<String> ENERGY_ID = SynchedEntityData.defineId(AbyssBurstEntity.class,
            EntityDataSerializers.STRING);

    public AbyssBurstEntity(EntityType<? extends AbyssBurstEntity> type, Level level) {
        super(type, level);
    }

    public AbyssBurstEntity(Level level, LivingEntity shooter, long energyAmount, ResourceLocation energyId) {
        super(ProjectKEntities.ABYSS_BURST_ENTITY.get(), shooter, level);
        this.entityData.set(ENERGY_AMOUNT, energyAmount);
        this.entityData.set(ENERGY_ID, energyId.toString());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ENERGY_AMOUNT, 0L);
        builder.define(ENERGY_ID, "projectk:abyss_energy");
    }

    public long getEnergyAmount() {
        return this.entityData.get(ENERGY_AMOUNT);
    }

    public ResourceLocation getEnergyId() {
        return ResourceLocation.parse(this.entityData.get(ENERGY_ID));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            spawnParticles();
        }

        if (this.tickCount > 16) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05, 0));
        } else {
            this.setNoGravity(true);
        }

        if (this.tickCount > 200) {
            this.discard();
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 2; i++) {
            this.level().addParticle(new AbyssBurstParticleOptions(getEnergyId()),
                    this.getX() + (random.nextDouble() - 0.5) * 0.2, this.getY() + (random.nextDouble() - 0.5) * 0.2,
                    this.getZ() + (random.nextDouble() - 0.5) * 0.2, 0, 0, 0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (target instanceof LivingEntity living) {
            float damage = (float) getEnergyAmount();
            DamageSource source = this.level().damageSources().magic();
            living.hurt(source, damage);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong(EnergyKeys.ENERGY_VALUE.toString(), getEnergyAmount());
        tag.putString(EnergyKeys.ENERGY_ID.toString(), getEnergyId().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(ENERGY_AMOUNT, tag.getLong(EnergyKeys.ENERGY_VALUE.toString()));
        if (tag.contains(EnergyKeys.ENERGY_ID.toString())) {
            this.entityData.set(ENERGY_ID, tag.getString(EnergyKeys.ENERGY_ID.toString()));
        }
    }
}
