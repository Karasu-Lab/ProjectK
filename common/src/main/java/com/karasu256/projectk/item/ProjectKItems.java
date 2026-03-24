package com.karasu256.projectk.item;

import com.karasu256.projectk.item.custom.ProjectKItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;

import static com.karasu256.projectk.registry.ItemsRegistry.item;

public class ProjectKItems {
    public static RegistrySupplier<Item> KARASIUM = item("karasium", () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)));
    public static RegistrySupplier<Item> RAW_KARASIUM = item("raw_karasium", () -> new ProjectKItem(new ProjectKItem.Properties()));
    public static RegistrySupplier<Item> KARASIUM_DUST = item("karasium_dust", () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)));
    public static RegistrySupplier<Item> WITHER_BONE = item("wither_bone", () -> new ProjectKItem(new ProjectKItem.Properties()));

    public static void init() {
    }
}
