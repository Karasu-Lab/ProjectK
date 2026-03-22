package com.karasu256.projectk.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ItemsRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static void register() {
        ITEMS.register();
    }

    public static RegistrySupplier<BlockItem> blockItem(RegistrySupplier<Block> block, Item.Properties properties) {
        return ITEMS.register(block.getId(), () -> new BlockItem(block.get(), properties));
    }
}
