package com.karasu256.projectk.block.entity.impl;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IAbyssNbtHelper {

    default void saveNbt(CompoundTag nbt, ResourceLocation key, Object value, HolderLookup.Provider registries) {
        if (value == null) return;
        String k = key.toString();
        if (value instanceof Integer i) {
            nbt.putInt(k, i);
        } else if (value instanceof Long l) {
            nbt.putLong(k, l);
        } else if (value instanceof String s) {
            nbt.putString(k, s);
        } else if (value instanceof ResourceLocation rl) {
            nbt.putString(k, rl.toString());
        } else if (value instanceof ItemStack stack) {
            nbt.put(k, stack.saveOptional(registries));
        } else if (value instanceof Boolean b) {
            nbt.putBoolean(k, b);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    default <T> T loadNbt(CompoundTag nbt, ResourceLocation key, Class<T> type, HolderLookup.Provider registries) {
        String k = key.toString();
        if (!nbt.contains(k)) return null;

        if (type == Integer.class) {
            return (T) Integer.valueOf(nbt.getInt(k));
        } else if (type == Long.class) {
            return (T) Long.valueOf(nbt.getLong(k));
        } else if (type == String.class) {
            return (T) nbt.getString(k);
        } else if (type == ResourceLocation.class) {
            return (T) ResourceLocation.parse(nbt.getString(k));
        } else if (type == ItemStack.class) {
            return (T) ItemStack.parseOptional(registries, nbt.getCompound(k));
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(nbt.getBoolean(k));
        }
        return null;
    }
}
