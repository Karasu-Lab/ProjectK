package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KItemRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 4)
public class ItemsRegistry implements IKRegistryTarget {
    public static void register() {
        KItemRegistry.register(ProjectK.MOD_ID);
    }

    public static @NotNull RegistrySupplier<BlockItem> blockItem(@NotNull RegistrySupplier<Block> block, Item.Properties properties) {
        return KItemRegistry.blockItem(ProjectK.MOD_ID, block, properties);
    }

    public static <T extends Item> @NotNull RegistrySupplier<T> item(String id, Supplier<T> item) {
        return CreativeTabsRegistry.tab(KItemRegistry.item(ProjectK.MOD_ID, id, item));
    }
}
