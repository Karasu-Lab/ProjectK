package com.karasu256.projectk.block;

import com.karasu256.projectk.block.custom.KGenerator;
import com.karasu256.projectk.block.custom.ProjectKBlock;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.karasu256.projectk.registry.BlocksRegistry.block;

public class ProjectKBlocks {
    public static RegistrySupplier<Block> K_GENERATOR = block("k_generator", KGenerator::new, new Item.Properties());
    public static RegistrySupplier<Block> KARSAIUM_ORE = block("karasium_ore", ProjectKBlock::new, new Item.Properties());
    public static RegistrySupplier<Block> DEEPSLATE_KARSAIUM_ORE = block("karasium_ore", () -> new ProjectKBlock(Block.Properties.ofFullCopy(Blocks.DEEPSLATE)), new Item.Properties());

    public static void init() {
    }
}
