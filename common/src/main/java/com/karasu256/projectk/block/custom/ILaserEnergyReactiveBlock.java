package com.karasu256.projectk.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ILaserEnergyReactiveBlock {
    void onLaserHit(Level level, BlockPos pos, BlockState state, ResourceLocation energyId, long amount);
}
