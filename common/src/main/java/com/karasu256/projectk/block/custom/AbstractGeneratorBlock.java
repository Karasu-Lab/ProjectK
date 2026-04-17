package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.registry.ProjectKProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGeneratorBlock extends AbstractProjectKEntityBlock implements IGeneratorBlock {
    public AbstractGeneratorBlock(Properties properties, ProjectKProperties<Block> pkProperties) {
        super(properties, pkProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
