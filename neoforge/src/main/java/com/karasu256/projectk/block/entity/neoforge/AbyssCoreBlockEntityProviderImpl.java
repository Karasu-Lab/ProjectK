package com.karasu256.projectk.block.entity.neoforge;

import com.karasu256.projectk.block.entity.AbyssCoreBlockEntity;
import com.karasu256.projectk.neoforge.block.entity.NeoForgeAbyssCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssCoreBlockEntityProviderImpl {
    public static AbyssCoreBlockEntity create(BlockPos pos, BlockState state) {
        return new NeoForgeAbyssCoreBlockEntity(pos, state);
    }
}
