package com.karasu256.projectk.exception;

import net.minecraft.world.level.block.Block;

public class EnergyNotFound extends IllegalStateException {
    public EnergyNotFound(Block block) {
        super("Block " + block + " does not define energy properties.");
    }
}
