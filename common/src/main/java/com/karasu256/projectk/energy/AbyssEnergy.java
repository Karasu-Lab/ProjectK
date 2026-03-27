package com.karasu256.projectk.energy;

import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record AbyssEnergy(long value) implements IEnergy {
    @Override
    public ResourceLocation getId() {
        return Id.id("abyss_energy");
    }

    @Override
    public long getValue() {
        return value;
    }

    public void readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
    }

    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putLong(getNbtId().toString(), value);
    }
}
