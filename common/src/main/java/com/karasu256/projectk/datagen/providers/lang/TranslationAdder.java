package com.karasu256.projectk.datagen.providers.lang;

import com.karasu256.projectk.registry.CreativeTabsRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.ItemLike;

@FunctionalInterface
public interface TranslationAdder {
    void add(String key, String value);

    default void add(ItemLike itemLike, String value) {
        add(itemLike.asItem().getDescriptionId(), value);
    }

    default void add(RegistrySupplier<? extends ItemLike> supplier, String value) {
        add(supplier.get(), value);
    }

    default void add(CreativeTabsRegistry.TabCategory category, String value) {
        add(category.getTranslationKey(), value);
    }

    default void add(CreativeTabsRegistry.TabCategory category) {
        add(category.getTranslationKey(), category.id().toUpperCase());
    }
}
