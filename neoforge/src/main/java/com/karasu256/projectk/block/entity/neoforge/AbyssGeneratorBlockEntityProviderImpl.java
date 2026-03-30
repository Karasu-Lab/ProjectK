package com.karasu256.projectk.neoforge.block.entity;

import com.karasu256.projectk.block.entity.AbyssGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntityProviderImpl {
    public static AbyssGeneratorBlockEntity create(BlockPos pos, BlockState state) {
        return new NeoForgeAbyssGeneratorBlockEntity(pos, state);
    }
}
