package com.karasu256.projectk.energy;

import com.karasu256.projectk.item.ProjectKItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;

public interface ITierInfo {
    String TIER_KEY = "tier";

    int getTier();

    void setTier(int tier);

    int getMaxTier();

    int getDefaultTier();

    default int clampTier(int tier) {
        int max = Math.max(1, getMaxTier());
        int next = Math.max(1, tier);
        return Math.min(next, max);
    }

    default boolean upgradeTier() {
        int current = getTier();
        int next = clampTier(current + 1);
        if (next == current) {
            return false;
        }
        setTier(next);
        return true;
    }

    default void loadTier(CompoundTag nbt) {
        int tier = nbt.contains(TIER_KEY) ? nbt.getInt(TIER_KEY) : getDefaultTier();
        setTier(clampTier(tier));
    }

    default void saveTier(CompoundTag nbt) {
        nbt.putInt(TIER_KEY, getTier());
    }

    default int getCraftTimeForTier(int baseTime) {
        int tier = Math.max(1, getTier());
        return Math.max(1, baseTime / tier);
    }

    default void dropTierUpgrades(Level level, BlockPos pos) {
        int count = Math.max(0, getTier() - getDefaultTier());
        for (int i = 0; i < count; i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    ProjectKItems.TIER_UPGRADE.get().getDefaultInstance());
        }
    }
}
