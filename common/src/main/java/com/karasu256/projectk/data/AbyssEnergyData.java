package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

public record AbyssEnergyData(ResourceLocation energyId, long amount) {
    public static final Codec<AbyssEnergyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("energy_id").forGetter(AbyssEnergyData::energyId),
            Codec.LONG.fieldOf("amount").forGetter(AbyssEnergyData::amount)
    ).apply(instance, AbyssEnergyData::new));
    public static final StreamCodec<ByteBuf, AbyssEnergyData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            AbyssEnergyData::energyId,
            ByteBufCodecs.VAR_LONG,
            AbyssEnergyData::amount,
            AbyssEnergyData::new
    );
    private static final String ENERGY_LIST_KEY = "projectk:abyss_energy_list";

    public static void applyToStack(ItemStack stack, ResourceLocation energyId, long amount) {
        if (stack == null || energyId == null) {
            return;
        }
        if (amount < 0) {
            removeFromStack(stack, energyId);
            return;
        }
        if (hasEnergyList(stack)) {
            List<AbyssEnergyData> list = readEnergyList(stack);
            int index = findEnergyIndex(list, energyId);
            if (index >= 0) {
                list.set(index, new AbyssEnergyData(energyId, amount));
            } else {
                list.add(new AbyssEnergyData(energyId, amount));
            }
            writeEnergyList(stack, list);
            return;
        }
        AbyssEnergyData existing = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (existing != null && existing.energyId() != null && !existing.energyId().equals(energyId)) {
            List<AbyssEnergyData> list = new ArrayList<>();
            list.add(existing);
            list.add(new AbyssEnergyData(energyId, amount));
            writeEnergyList(stack, list);
            return;
        }
        stack.set(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get(), new AbyssEnergyData(energyId, amount));
    }

    private static void removeFromStack(ItemStack stack, ResourceLocation energyId) {
        if (!hasEnergyList(stack)) {
            stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            return;
        }
        List<AbyssEnergyData> list = readEnergyList(stack);
        int index = findEnergyIndex(list, energyId);
        if (index >= 0) {
            list.remove(index);
            writeEnergyList(stack, list);
        } else {
            stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        }
    }

    private static boolean hasEnergyList(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(ENERGY_LIST_KEY, Tag.TAG_LIST);
    }

    private static int findEnergyIndex(List<AbyssEnergyData> list, ResourceLocation id) {
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data != null && id.equals(data.energyId())) {
                return i;
            }
        }
        return -1;
    }

    private static List<AbyssEnergyData> readEnergyList(ItemStack stack) {
        List<AbyssEnergyData> list = new ArrayList<>();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(ENERGY_LIST_KEY, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(ENERGY_LIST_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                AbyssEnergyData.CODEC.parse(NbtOps.INSTANCE, listTag.getCompound(i)).result().ifPresent(list::add);
            }
        }
        if (list.isEmpty()) {
            AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            if (data != null && data.amount() > 0 && data.energyId() != null) {
                list.add(data);
            }
        }
        return list;
    }

    private static void writeEnergyList(ItemStack stack, List<AbyssEnergyData> list) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (list.isEmpty()) {
            stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            tag.remove(ENERGY_LIST_KEY);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }
        if (list.size() == 1) {
            AbyssEnergyData data = list.get(0);
            stack.set(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get(), data);
            tag.remove(ENERGY_LIST_KEY);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }
        stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        ListTag listTag = new ListTag();
        for (AbyssEnergyData data : list) {
            AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(listTag::add);
        }
        tag.put(ENERGY_LIST_KEY, listTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
