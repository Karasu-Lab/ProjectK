package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.item.ProjectKItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;
import java.util.function.BiConsumer;

public class CommonBlockLootProvider extends BlockLootSubProvider {

    public CommonBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(ProjectKBlocks.KARASIUM_ORE.get(), block -> createOreDrop(block, ProjectKItems.RAW_KARASIUM.get()));
        this.add(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.get(), block -> createOreDrop(block, ProjectKItems.RAW_KARASIUM.get()));
        this.dropSelf(ProjectKBlocks.K_GENERATOR.get());
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        this.generate();
        this.map.forEach(biConsumer);
        this.map.clear();
    }
}
