package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.registry.ProjectKTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
public class CommonBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {

    public CommonBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.BLOCK, lookupProvider, block -> block.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ProjectKBlocks.KARASIUM_ORE.getKey()).add(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ProjectKBlocks.KARASIUM_ORE.getKey()).add(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.getKey());

        tag(ProjectKTags.Blocks.IN_BIOME_IN_BLOCK_CRAFTING).add(Blocks.FIRE).add(Blocks.SOUL_FIRE);
    }
}
