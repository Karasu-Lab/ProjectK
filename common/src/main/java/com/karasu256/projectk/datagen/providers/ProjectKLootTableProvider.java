package com.karasu256.projectk.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ProjectKLootTableProvider extends LootTableProvider {
    public ProjectKLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(CommonBlockLootProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(CommonEntityLootProvider::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(r -> new CommonChestLootProvider(), LootContextParamSets.CHEST)
        ), registries);
    }

}
