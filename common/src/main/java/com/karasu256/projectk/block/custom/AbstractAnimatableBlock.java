package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.registry.ProjectKProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAnimatableBlock extends AbstractGeneratorBlock {
    public AbstractAnimatableBlock(Properties properties, ProjectKProperties<Block> pkProperties) {
        super(properties, pkProperties);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
