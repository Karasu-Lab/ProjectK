package com.karasu256.projectk.block.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntityProvider {
    @ExpectPlatform
    public static AbyssGeneratorBlockEntity create(BlockPos pos, BlockState state) {
        throw new AssertionError();
    }
}
