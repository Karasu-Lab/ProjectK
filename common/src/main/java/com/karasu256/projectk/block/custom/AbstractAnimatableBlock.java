package com.karasu256.projectk.block.custom;

import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAnimatableBlock extends AbstractGeneratorBlock {
    public AbstractAnimatableBlock(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
