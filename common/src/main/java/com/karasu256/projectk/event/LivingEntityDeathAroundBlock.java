package com.karasu256.projectk.event;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface LivingEntityDeathAroundBlock {
    int getRadius();

    boolean shouldTrigger(LivingEntityDeathAroundBlock invoker, int radius, LivingEntity entity, BlockPos pos, Level level);

    Block getBlock();

    EventResult die(LivingEntity entity, BlockPos pos, Level level);
}
