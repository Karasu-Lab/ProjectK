package com.karasu256.projectk.block.custom;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class KGenerator extends AbstractGeneratorBlock {
    public KGenerator() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
    }
}
