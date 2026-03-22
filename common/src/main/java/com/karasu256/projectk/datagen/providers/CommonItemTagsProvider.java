package com.karasu256.projectk.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class CommonItemTagsProvider extends ItemTagsProvider {

    public CommonItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
        super(output, lookupProvider, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Implement default common item tags here
    }
}
