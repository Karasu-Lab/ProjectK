package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.block.ProjectKBlocks;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CommonBlockStateProvider {

    public interface Generator {
        void simpleBlock(Block block);

        void simpleBlockItem(Block block);

        void cubeBottomTop(Block block, String base, String side, String bottom, String top);
    }

    public static void generate(@NotNull Generator generator) {
        generator.simpleBlock(ProjectKBlocks.KARASIUM_ORE.get());
        generator.simpleBlockItem(ProjectKBlocks.KARASIUM_ORE.get());

        generator.simpleBlock(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.get());
        generator.simpleBlockItem(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.get());

        generator.cubeBottomTop(ProjectKBlocks.ABYSS_GENERATOR.get(), "abyss_generator", "abyss_generator_side", "abyss_generator_bottom", "abyss_generator_top");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_GENERATOR.get());

        generator.simpleBlock(ProjectKBlocks.ABYSS_CORE.get());
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_CORE.get());
    }
}
