package com.karasu256.projectk.registry;

import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ItemsRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static void register() {
        ITEMS.register();
    }

    public static @NotNull RegistrySupplier<BlockItem> blockItem(@NotNull RegistrySupplier<Block> block, Item.Properties properties) {
        return item(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }

    public static <T extends Item> @NotNull RegistrySupplier<T> item(String id, Supplier<T> item) {
        return CreativeTabsRegistry.tab(ITEMS.register(Id.id(id), item));
    }
}
