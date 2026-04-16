package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KBlockRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static com.karasu256.projectk.registry.ItemsRegistry.blockItem;

@KRegistry(modId = ProjectK.MOD_ID, order = 3)
public class BlocksRegistry implements IKRegistryTarget {
    public static void register() {
        KBlockRegistry.register(ProjectK.MOD_ID);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> block(String id, Supplier<T> block) {
        return (RegistrySupplier<T>) KBlockRegistry.block(ProjectK.MOD_ID, id, (Supplier<Block>) block);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> block(String id, Supplier<T> block, Item.Properties itemProperties) {
        return block(id, block, itemProperties, false, CreativeTabsRegistry.GENERAL);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> block(String id, Supplier<T> block, Item.Properties itemProperties, CreativeTabsRegistry.TabCategory category) {
        return block(id, block, itemProperties, false, category);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> block(String id, Supplier<T> block, Item.Properties itemProperties, boolean energySuffixItem) {
        return block(id, block, itemProperties, energySuffixItem, CreativeTabsRegistry.GENERAL);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> RegistrySupplier<T> block(String id, Supplier<T> block, Item.Properties itemProperties, boolean energySuffixItem, CreativeTabsRegistry.TabCategory category) {
        RegistrySupplier<T> registeredBlock = (RegistrySupplier<T>) KBlockRegistry.block(ProjectK.MOD_ID, id,
                (Supplier<Block>) block);
        blockItem((RegistrySupplier<Block>) registeredBlock, itemProperties, energySuffixItem, category);
        return registeredBlock;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block, I extends Item> RegistrySupplier<T> block(String id, Supplier<T> block, java.util.function.Function<T, I> itemFactory) {
        return block(id, block, itemFactory, false, CreativeTabsRegistry.GENERAL);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block, I extends Item> RegistrySupplier<T> block(String id, Supplier<T> block, java.util.function.Function<T, I> itemFactory, CreativeTabsRegistry.TabCategory category) {
        return block(id, block, itemFactory, false, category);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block, I extends Item> RegistrySupplier<T> block(String id, Supplier<T> block, java.util.function.Function<T, I> itemFactory, boolean energySuffixItem) {
        return block(id, block, itemFactory, energySuffixItem, CreativeTabsRegistry.GENERAL);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block, I extends Item> RegistrySupplier<T> block(String id, Supplier<T> block, java.util.function.Function<T, I> itemFactory, boolean energySuffixItem, CreativeTabsRegistry.TabCategory category) {
        RegistrySupplier<T> registeredBlock = (RegistrySupplier<T>) KBlockRegistry.block(ProjectK.MOD_ID, id,
                (Supplier<Block>) block);
        ItemsRegistry.item(id, () -> itemFactory.apply(registeredBlock.get()), energySuffixItem, category);
        return registeredBlock;
    }
}
