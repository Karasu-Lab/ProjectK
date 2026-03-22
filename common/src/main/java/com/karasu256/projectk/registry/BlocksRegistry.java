package com.karasu256.projectk.registry;

import com.karasu256.projectk.block.custom.KGenerator;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.karasu256.projectk.ProjectK.MOD_ID;
import static com.karasu256.projectk.registry.ItemsRegistry.blockItem;

public class BlocksRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);
    static RegistrySupplier<Block> K_GENERATOR = block("k_generator", KGenerator::new, new Item.Properties());

    public static void register() {
        BLOCKS.register();
    }

    @NotNull
    private static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    private static RegistrySupplier<Block> block(String id, Supplier<Block> block) {
        return BLOCKS.register(id(id), block);
    }

    private static RegistrySupplier<Block> block(String id, Supplier<Block> block, Item.Properties itemProperties) {
        RegistrySupplier<Block> registeredBlock = BLOCKS.register(id(id), block);
        blockItem(registeredBlock, itemProperties);
        return registeredBlock;
    }
}
