package com.karasu256.projectk.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;

import static com.karasu256.projectk.registry.ItemsRegistry.item;

public class ProjectKItems {
    public static RegistrySupplier<Item> EXAMPLE_ITEM = item("example_item", () -> new Item(new Item.Properties()));

    public static void init() {
    }
}
