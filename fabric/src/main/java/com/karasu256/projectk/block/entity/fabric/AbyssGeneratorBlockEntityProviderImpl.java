package com.karasu256.projectk.block.entity.fabric;

import com.karasu256.projectk.block.entity.AbyssGeneratorBlockEntity;
import com.karasu256.projectk.fabric.block.entity.FabricAbyssGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssGeneratorBlockEntityProviderImpl {
    public static AbyssGeneratorBlockEntity create(BlockPos pos, BlockState state) {
        return new FabricAbyssGeneratorBlockEntity(pos, state);
    }
}
