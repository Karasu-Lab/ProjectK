package com.karasu256.projectk.energy;

import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEnergyBlockEntitySync {
    default void markDirtyAndSync() {
        if (this instanceof BlockEntity be) {
            be.setChanged();
            if (be instanceof KarasuCoreBlockEntity kbe) {
                kbe.sync();
            } else {
                if (be.getLevel() != null && !be.getLevel().isClientSide) {
                    be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                }
            }
        }
    }
}
