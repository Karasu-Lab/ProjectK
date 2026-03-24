package com.karasu256.projectk.mixin;

import com.karasu256.projectk.item.ProjectKItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void handleTransformation(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> ci) {
        if (this.level().isClientSide || isRemoved()) return;

        if (damageSource.is(DamageTypeTags.IS_FIRE)) {
            BlockPos pos = this.blockPosition();
            if (this.level().getBiome(pos).is(Biomes.SOUL_SAND_VALLEY)) {
                ItemStack itemStack = this.getItem();
                if (itemStack.is(ProjectKItems.WITHER_BONE.get())) {
                    this.projectK$tryTransform(Items.POPPY, ci);
                } else if (itemStack.is(Items.POPPY)) {
                    this.projectK$tryTransform(ProjectKItems.WITHER_BONE.get(), ci);
                }
            }
        }
    }

    @Unique
    private void projectK$tryTransform(net.minecraft.world.item.Item partnerItem, CallbackInfoReturnable<Boolean> ci) {
        AABB aabb = this.getBoundingBox().inflate(1.0);
        List<ItemEntity> partners = this.level().getEntitiesOfClass(ItemEntity.class, aabb,
                e -> (Object) e != (Object) this && !e.isRemoved() && e.getItem().is(partnerItem));

        if (!partners.isEmpty()) {
            ItemEntity partner = partners.getFirst();

            this.discard();
            partner.discard();

            ItemStack resultStack = new ItemStack(Items.WITHER_ROSE);
            resultStack.set(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
            ItemEntity result = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), resultStack);
            this.level().addFreshEntity(result);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(), this.getZ(), 20, 0.2, 0.2, 0.2, 0.05);
                serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 10, 0.2, 0.2, 0.2, 0.02);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 0.5f, 1.0f);

            ci.setReturnValue(false);
            ci.cancel();
        }
    }
}
