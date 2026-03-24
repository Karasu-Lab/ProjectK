package com.karasu256.projectk.mixin;

import com.karasu256.projectk.event.LivingEntityDeathAroundBlock;
import com.karasu256.projectk.event.ProjectKEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Contract(pure = true)
    @Inject(method = "remove", at = @At("HEAD"))
    private void remove(@NotNull Entity.RemovalReason removalReason, CallbackInfo ci) {
        var entity = (LivingEntity) (Object) this;
        var level = entity.level();
        if (removalReason.equals(Entity.RemovalReason.KILLED) && !level.isClientSide) {
            LivingEntityDeathAroundBlock invoker = ProjectKEntityEvents.LIVING_DEATH_AROUND_ANY_BLOCK.invoker();
            BlockPos entityPos = entity.blockPosition();
            int radius = invoker.getRadius();
            if (invoker.shouldTrigger(invoker, radius, entity, entityPos, level)) {
                invoker.die(entity, entityPos, level);
            }
        }
    }
}
