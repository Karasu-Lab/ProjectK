package com.karasu256.projectk.api.datagen.impl;

import com.google.gson.JsonElement;
import com.karasu256.projectk.data.AbyssEnergySpawnRule;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

public abstract class AbstractAbyssEnergySpawnRuleProvider extends AbstractProjectKDataProvider<AbyssEnergySpawnRule> {
    private CachedOutput cache;

    public AbstractAbyssEnergySpawnRuleProvider(PackOutput output, String name) {
        super(output, name);
    }

    protected void createCache(CachedOutput output) {
        this.cache = output;
    }

    public void addData(String id, AbyssEnergySpawnRule data) {
        write(cache, id, data);
    }

    @Override
    public JsonElement getElement(AbyssEnergySpawnRule data) {
        return AbyssEnergySpawnRule.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow();
    }
}
