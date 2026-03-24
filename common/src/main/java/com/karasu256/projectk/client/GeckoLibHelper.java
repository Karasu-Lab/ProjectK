package com.karasu256.projectk.client;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface GeckoLibHelper {
    <T extends BlockEntity> void registerBlockRenderer(BlockEntityType<T> type);
}
