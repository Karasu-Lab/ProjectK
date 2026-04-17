package com.karasu256.projectk.item;

import net.minecraft.world.item.CreativeModeTab;

public interface IVariantItem {
    default void displayVariants(CreativeModeTab.Output output) {}

    default boolean shouldSkipDefault() {
        return false;
    }
}
