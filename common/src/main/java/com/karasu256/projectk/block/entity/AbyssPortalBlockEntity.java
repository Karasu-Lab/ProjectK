package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssPortalBlockEntity extends AbstractAbyssMachineBlockEntity {
    public AbyssPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_PORTAL.get(), pos, state, 1000000L, 64);
        addItemSlot(Id.id("prism_extension"));
    }
}
