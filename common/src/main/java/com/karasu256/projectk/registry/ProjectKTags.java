package com.karasu256.projectk.registry;

import com.karasu256.projectk.utils.Id;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ProjectKTags {
    public static class Blocks {
        public static final TagKey<Block> IN_BIOME_IN_BLOCK_CRAFTING = TagKey.create(Registries.BLOCK, Id.id("in_biome_in_block_crafting"));
    }

    public static class Items {
        public static final TagKey<Item> BOOKS = TagKey.create(Registries.ITEM, Id.id("books"));
    }
}
