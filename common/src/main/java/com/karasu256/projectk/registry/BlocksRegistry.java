package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KBlockRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static com.karasu256.projectk.registry.ItemsRegistry.blockItem;

@KRegistry(modId = ProjectK.MOD_ID, order = 3)
public class BlocksRegistry implements IKRegistryTarget {
    public static void register() {
        KBlockRegistry.register(ProjectK.MOD_ID);
    }

    public static RegistrySupplier<Block> block(String id, Supplier<Block> block) {
        return KBlockRegistry.block(ProjectK.MOD_ID, id, block);
    }

    public static RegistrySupplier<Block> block(String id, Supplier<Block> block, Item.Properties itemProperties) {
        RegistrySupplier<Block> registeredBlock = KBlockRegistry.block(ProjectK.MOD_ID, id, block);
        blockItem(registeredBlock, itemProperties);
        return registeredBlock;
    }
}
