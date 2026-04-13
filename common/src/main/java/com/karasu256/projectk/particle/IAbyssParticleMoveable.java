package com.karasu256.projectk.particle;

import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public interface IAbyssParticleMoveable extends ICableInputable, ICapacity {
    default long acceptEnergyFromParticle(ResourceLocation id, long amount, boolean simulate, Direction side) {
        return insert(id, amount, simulate, side);
    }
}
