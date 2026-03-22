package com.karasu256.projectk.block;

import com.karasu256.projectk.block.custom.KGenerator;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.karasu256.projectk.registry.BlocksRegistry.block;

public class ProjectKBlocks {
    public static RegistrySupplier<Block> K_GENERATOR = block("k_generator", KGenerator::new, new Item.Properties());

    public static void init() {
    }
}
