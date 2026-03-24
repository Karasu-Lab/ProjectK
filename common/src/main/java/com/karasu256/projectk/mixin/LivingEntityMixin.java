package com.karasu256.projectk.mixin;

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
            BlockPos entityPos = entity.blockPosition();
            var invoker = ProjectKEntityEvents.LIVING_DEATH_AROUND_ANY_BLOCK.invoker();
            var radius = invoker.getRadius();
            for (BlockPos pos : BlockPos.betweenClosed(entityPos.offset(-radius, -radius, -radius), entityPos.offset(radius, radius, radius))) {
                if (level.getBlockState(pos).is(invoker.getBlock())) {
                    invoker.die(entity, entityPos, level);
                }
            }
        }
    }
}
