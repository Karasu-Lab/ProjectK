package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.item.IVariantItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ProjectKBlockItem extends BlockItem implements IVariantItem {
    public ProjectKBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
}
