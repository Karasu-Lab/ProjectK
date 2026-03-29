package com.karasu256.projectk.block;

import com.karasu256.projectk.block.custom.AbyssCore;
import com.karasu256.projectk.block.custom.AbyssGenerator;
import com.karasu256.projectk.block.custom.ProjectKBlock;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.karasu256.projectk.registry.BlocksRegistry.block;

public class ProjectKBlocks {
    public static RegistrySupplier<Block> ABYSS_GENERATOR = block("abyss_generator", () -> new AbyssGenerator(10000L), new Item.Properties());
    public static RegistrySupplier<Block> ABYSS_CORE = block("abyss_core", () -> new AbyssCore(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.STONE)), new Item.Properties());
    public static RegistrySupplier<Block> KARASIUM_ORE = block("karasium_ore", ProjectKBlock::new, new Item.Properties());
    public static RegistrySupplier<Block> DEEPSLATE_KARASIUM_ORE = block("deepslate_karasium_ore", () -> new ProjectKBlock(Block.Properties.ofFullCopy(Blocks.DEEPSLATE)), new Item.Properties());

    public static void init() {
    }
}
