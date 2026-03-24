package com.karasu256.projectk.event;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("UnusedReturnValue")
public interface LivingDeathAroundBlock {
    default int getRadius() {
        return 5;
    }

    default Block getBlock() {
        return Blocks.WITHER_ROSE;
    }

    EventResult die(LivingEntity entity, BlockPos pos, Level level);
}
