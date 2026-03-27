package com.karasu256.projectk.energy;

import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AbyssEnergy implements IEnergy {
    private long value;

    public AbyssEnergy(long value) {
        this.value = value;
    }

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
