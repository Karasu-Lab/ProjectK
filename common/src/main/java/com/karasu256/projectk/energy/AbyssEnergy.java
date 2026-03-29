package com.karasu256.projectk.energy;

import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.data.impl.EnergyValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AbyssEnergy extends EnergyValue implements IProjectKEnergy {

    public AbyssEnergy(ResourceLocation id, long value) {
        super(id);
        this.value = value;
    }

    public AbyssEnergy(long value) {
        this(Id.id("abyss_energy"), value);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    public long insert(ResourceLocation id, long maxAmount, long maxCapacity, boolean simulate) {
        if (value > 0 && !getId().equals(id)) {
            return 0;
        }
        long energyReceived = Math.min(maxCapacity - value, maxAmount);
        if (energyReceived <= 0) return 0;
        if (!simulate) {
            if (value == 0) {
                setId(id);
            }
            setValue(value + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        return insert(id, maxAmount, Long.MAX_VALUE, simulate);
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        if (!getId().equals(id)) {
            return 0;
        }
        long energyExtracted = Math.min(value, maxAmount);
        if (!simulate) {
            setValue(value - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public Component getName() {
        return Component.translatable(getTranslationKey());
    }

    @Override
    public Component getFormatted() {
        Component name = getName();
        return name == null ? Component.empty() : Component.translatable("energy.projectk.abyss_energy_format", name);
    }

    @Override
    public void readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        if (nbt.contains(EnergyKeys.ENERGY_ID.toString())) {
            ResourceLocation.CODEC.parse(NbtOps.INSTANCE, nbt.get(EnergyKeys.ENERGY_ID.toString())).resultOrPartial().ifPresent(rl -> id = rl);
        }
        if (nbt.contains(EnergyKeys.ENERGY_VALUE.toString())) {
            value = nbt.getLong(EnergyKeys.ENERGY_VALUE.toString());
        }
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        if (id != null) {
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, id).resultOrPartial().ifPresent(tag -> nbt.put(EnergyKeys.ENERGY_ID.toString(), tag));
        }
        nbt.putLong(EnergyKeys.ENERGY_VALUE.toString(), value);
    }
}
