package com.karasu256.projectk.registry;

import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static com.karasu256.projectk.ProjectK.MOD_ID;
import static com.karasu256.projectk.registry.ItemsRegistry.blockItem;

public class BlocksRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);

    public static void register() {
        BLOCKS.register();
    }


    public static RegistrySupplier<Block> block(String id, Supplier<Block> block) {
        return BLOCKS.register(Id.id(id), block);
    }

    public static RegistrySupplier<Block> block(String id, Supplier<Block> block, Item.Properties itemProperties) {
        RegistrySupplier<Block> registeredBlock = BLOCKS.register(Id.id(id), block);
        blockItem(registeredBlock, itemProperties);
        return registeredBlock;
    }
}
