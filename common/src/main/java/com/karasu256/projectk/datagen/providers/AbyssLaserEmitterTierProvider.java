package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonElement;
import com.karasu256.projectk.api.tier.PKTiers;
import com.karasu256.projectk.data.AbyssLaserEmitterTier;
import com.karasu256.projectk.api.datagen.impl.AbstractAbyssLaserEmitterTierProvider;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AbyssLaserEmitterTierProvider extends AbstractAbyssLaserEmitterTierProvider {
    public AbyssLaserEmitterTierProvider(PackOutput output) {
        super(output, "abyss_laser_emitter_tiers");
    }

    @Override
    public CompletableFuture<?> runDataGen(CachedOutput cachedOutput) {
        add(PKTiers.TIER_0, 30000L, 100L, 100, 20);
        add(PKTiers.TIER_1, 60000L, 200L, 200, 15);
        add(PKTiers.TIER_2, 120000L, 400L, 400, 10);
        add(PKTiers.TIER_3, 240000L, 800L, 800, 5);
        return super.runDataGen(cachedOutput);
    }

    @Override
    public JsonElement getElement(AbyssLaserEmitterTier data) {
        return AbyssLaserEmitterTier.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow();
    }
}
