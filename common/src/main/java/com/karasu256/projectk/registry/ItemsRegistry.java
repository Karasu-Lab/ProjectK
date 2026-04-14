package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KItemRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 4)
public class ItemsRegistry implements IKRegistryTarget {
    private static final Set<ResourceLocation> ENERGY_SUFFIX_ITEMS = new LinkedHashSet<>();

    public static void register() {
        KItemRegistry.register(ProjectK.MOD_ID);
    }

    public static @NotNull RegistrySupplier<BlockItem> blockItem(@NotNull RegistrySupplier<Block> block, Item.Properties properties) {
        return blockItem(block, properties, false);
    }

    public static @NotNull RegistrySupplier<BlockItem> blockItem(@NotNull RegistrySupplier<Block> block, Item.Properties properties, boolean energySuffixTexture) {
        RegistrySupplier<BlockItem> registered = CreativeTabsRegistry.tab(
                KItemRegistry.blockItem(ProjectK.MOD_ID, block, properties));
        if (energySuffixTexture) {
            ENERGY_SUFFIX_ITEMS.add(block.getId());
        }
        return registered;
    }

    public static <T extends Item> @NotNull RegistrySupplier<T> item(String id, Supplier<T> item) {
        return CreativeTabsRegistry.tab(KItemRegistry.item(ProjectK.MOD_ID, id, item));
    }

    public static <T extends Item> @NotNull RegistrySupplier<T> item(String id, Supplier<T> item, boolean energySuffixTexture) {
        RegistrySupplier<T> registered = CreativeTabsRegistry.tab(KItemRegistry.item(ProjectK.MOD_ID, id, item));
        if (energySuffixTexture) {
            ENERGY_SUFFIX_ITEMS.add(Id.id(id));
        }
        return registered;
    }

    public static Set<ResourceLocation> getEnergySuffixItems() {
        return Collections.unmodifiableSet(ENERGY_SUFFIX_ITEMS);
    }
}
