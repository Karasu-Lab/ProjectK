package com.karasu256.projectk.block.custom;

import net.minecraft.world.level.block.Blocks;

public class ProjectKBlock extends AbstractProjectKBlock{
    public ProjectKBlock() {
        super(Properties.ofFullCopy(Blocks.STONE));
    }

    public ProjectKBlock(Properties properties) {
        super(properties);
    }
}
