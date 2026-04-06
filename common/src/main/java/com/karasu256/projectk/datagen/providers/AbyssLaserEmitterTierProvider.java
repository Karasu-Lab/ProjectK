package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.data.AbyssLaserEmitterTier;
import com.karasu256.projectk.utils.Id;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class AbyssLaserEmitterTierProvider implements DataProvider {
    private final PathProvider pathProvider;

    public AbyssLaserEmitterTierProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "abyss_laser_emitter_tiers");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        CompletableFuture<?> t0 = write(output, "tier_0", new AbyssLaserEmitterTier(0, 30000L, 100L, 100, 20));
        CompletableFuture<?> t1 = write(output, "tier_1", new AbyssLaserEmitterTier(1, 60000L, 200L, 200, 15));
        CompletableFuture<?> t2 = write(output, "tier_2", new AbyssLaserEmitterTier(2, 120000L, 400L, 400, 10));
        CompletableFuture<?> t3 = write(output, "tier_3", new AbyssLaserEmitterTier(3, 240000L, 800L, 800, 5));
        return CompletableFuture.allOf(t0, t1, t2, t3);
    }

    @Override
    public @NotNull String getName() {
        return "ProjectK Abyss Laser Emitter Tiers";
    }

    private CompletableFuture<?> write(CachedOutput output, String id, AbyssLaserEmitterTier tier) {
        return DataProvider.saveStable(output, AbyssLaserEmitterTier.CODEC.encodeStart(JsonOps.INSTANCE, tier).getOrThrow(), pathProvider.json(Id.id(id)));
    }
}
