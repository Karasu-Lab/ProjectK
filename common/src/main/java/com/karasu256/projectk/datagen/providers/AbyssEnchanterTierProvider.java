package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonElement;
import com.karasu256.projectk.data.AbyssEnchanterTier;
import com.karasu256.projectk.utils.Id;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class AbyssEnchanterTierProvider implements DataProvider {
    private final PathProvider pathProvider;

    public AbyssEnchanterTierProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "abyss_enchant_tiers");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        CompletableFuture<?> t10 = write(output, "tier_10", new AbyssEnchanterTier(10, 10000L));
        CompletableFuture<?> t20 = write(output, "tier_20", new AbyssEnchanterTier(20, 20000L));
        CompletableFuture<?> t30 = write(output, "tier_30", new AbyssEnchanterTier(30, 30000L));
        return CompletableFuture.allOf(t10, t20, t30);
    }

    @Override
    public String getName() {
        return "ProjectK Abyss Enchanter Tiers";
    }

    private CompletableFuture<?> write(CachedOutput output, String id, AbyssEnchanterTier tier) {
        JsonElement json = AbyssEnchanterTier.CODEC.encodeStart(JsonOps.INSTANCE, tier).getOrThrow();
        Path path = pathProvider.json(Id.id(id));
        return DataProvider.saveStable(output, json, path);
    }
}
