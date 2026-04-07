package com.karasu256.projectk.block.entity.impl;

import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractAbyssNbtBlockEntity extends KarasuCoreBlockEntity implements IAbyssNbtHelper {

    protected AbstractAbyssNbtBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
