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
        RegistrySupplier<T> registeredBlock = (RegistrySupplier<T>) KBlockRegistry.block(ProjectK.MOD_ID, id, (Supplier<Block>) block);
        blockItem((RegistrySupplier<Block>) registeredBlock, itemProperties);
        return registeredBlock;
    }
}
