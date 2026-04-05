package com.karasu256.projectk.energy;

import com.karasu256.projectk.data.AbyssEnergyData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public interface IMultiEnergyStorage extends IEnergyListHolder {
    String ENERGY_LIST_KEY = "projectk:abyss_energy_list";

    List<AbyssEnergyData> getEnergyList();

    long getEnergyCapacity();

    int getMaxEnergyTypes();

    default int getActiveEnergyIndex() {
        return -1;
    }

    default AbyssEnergyData getEnergyByIndex(int index) {
        List<AbyssEnergyData> list = getEnergyList();
        if (index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    default int getEnergyTypeCount() {
        int count = 0;
        for (AbyssEnergyData data : getEnergyList()) {
            if (data != null && data.energyId() != null && data.amount() > 0) {
                count++;
            }
        }
        return count;
    }

    default List<EnergyEntry> getEnergyEntries() {
        List<EnergyEntry> entries = new ArrayList<>();
        List<AbyssEnergyData> list = getEnergyList();
        int activeIndex = getActiveEnergyIndex();
        long capacity = getEnergyCapacity();
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data == null || data.energyId() == null || data.amount() <= 0) {
                continue;
            }
            entries.add(new EnergyEntry(data.energyId(), data.amount(), capacity, i == activeIndex));
        }
        return entries;
    }

    default void readEnergyListNbt(CompoundTag nbt) {
        List<AbyssEnergyData> list = getEnergyList();
        list.clear();
        if (nbt.contains(ENERGY_LIST_KEY, Tag.TAG_LIST)) {
            ListTag listTag = nbt.getList(ENERGY_LIST_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                AbyssEnergyData.CODEC.parse(NbtOps.INSTANCE, listTag.getCompound(i)).result().ifPresent(list::add);
            }
        }
    }

    default void writeEnergyListNbt(CompoundTag nbt) {
        List<AbyssEnergyData> list = getEnergyList();
        if (list.isEmpty()) {
            nbt.remove(ENERGY_LIST_KEY);
            return;
        }
        ListTag listTag = new ListTag();
        for (AbyssEnergyData data : list) {
            AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(listTag::add);
        }
        nbt.put(ENERGY_LIST_KEY, listTag);
    }

    default int findEnergyIndex(ResourceLocation id) {
        if (id == null) {
            return -1;
        }
        List<AbyssEnergyData> list = getEnergyList();
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data != null && id.equals(data.energyId())) {
                return i;
            }
        }
        return -1;
    }
}
