package com.karasu256.projectk.block.entity;

import net.karasuniki.karasunikilib.api.block.entity.impl.AbstractContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractPKContainerBlockEntity extends AbstractContainerBlockEntity {
    public AbstractPKContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
