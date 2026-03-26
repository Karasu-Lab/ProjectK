package com.karasu256.projectk.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractContainerBlockEntity extends com.karasu256.karasulab.karasucore.api.block.entity.impl.AbstractContainerBlockEntity {
    public AbstractContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
