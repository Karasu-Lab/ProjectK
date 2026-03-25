package com.karasu256.projectk.api.nbt;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface INbtWritable {
    void writeNbt(CompoundTag nbt, HolderLookup.Provider registries);
}
