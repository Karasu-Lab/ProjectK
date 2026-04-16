package com.karasu256.projectk.mixin.entity;

import com.karasu256.projectk.api.entity.ProjectKPowerable;
import com.karasu256.projectk.utils.Id;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ProjectKPowerable, PowerableMob {
    @Override
    public boolean isPowered() {
        return this.projectk$isCharged();
    }

    @Unique
    private static final String CHARGED = Id.id("charged").toString();

    @Unique
    private static final EntityDataAccessor<Boolean> projectk$DATA_CHARGED = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.BOOLEAN);

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean projectk$isCharged() {
        return this.entityData.get(projectk$DATA_CHARGED);
    }

    @Override
    public void projectk$setCharged(boolean charged) {
        this.entityData.set(projectk$DATA_CHARGED, charged);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void projectk$onDefineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(projectk$DATA_CHARGED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void projectk$onAddAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean(CHARGED, this.projectk$isCharged());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void projectk$onReadAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains(CHARGED)) {
            this.projectk$setCharged(nbt.getBoolean(CHARGED));
        }
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        super.thunderHit(world, lightning);
        this.projectk$setCharged(true);
    }
}
