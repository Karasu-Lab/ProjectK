package com.karasu256.projectk.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.Set;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class CommonBlockLootProvider extends BlockLootSubProvider {

    public CommonBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // Implement default common block loot tables here
    }

    protected Iterable<Block> getKnownBlocks() {
        return Collections.emptyList();
    }
}
