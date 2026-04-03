package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonElement;
import com.karasu256.projectk.data.AbyssEnergySpawnRule;
import com.karasu256.projectk.data.spawn.BlockCondition;
import com.karasu256.projectk.data.spawn.EnergyAmountRule;
import com.karasu256.projectk.data.spawn.MobCondition;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.utils.Id;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AbyssEnergySpawnRuleProvider implements DataProvider {
    private final PathProvider pathProvider;

    public AbyssEnergySpawnRuleProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "abyss_energy_spawns");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        AbyssEnergySpawnRule witherRoseRule = new AbyssEnergySpawnRule(
            ProjectKEnergies.ABYSS.id(),
            new MobCondition(List.of(), List.of(), List.of()),
            new BlockCondition(5, List.of(), List.of(ResourceLocation.withDefaultNamespace("wither_rose"))),
            new EnergyAmountRule(EnergyAmountRule.AmountType.BUILT_IN, 0L)
        );
        AbyssEnergySpawnRule yinRule = new AbyssEnergySpawnRule(
                ProjectKEnergies.YIN.id(),
                new MobCondition(List.of(MobCategory.MONSTER), List.of(), List.of()),
                BlockCondition.ANY,
                new EnergyAmountRule(EnergyAmountRule.AmountType.BUILT_IN, 0L)
        );

        AbyssEnergySpawnRule yangRule = new AbyssEnergySpawnRule(
                ProjectKEnergies.YANG.id(),
                new MobCondition(List.of(MobCategory.CREATURE, MobCategory.WATER_CREATURE, MobCategory.AXOLOTLS, MobCategory.UNDERGROUND_WATER_CREATURE), List.of(), List.of()),
                BlockCondition.ANY,
                new EnergyAmountRule(EnergyAmountRule.AmountType.BUILT_IN, 0L)
        );

        CompletableFuture<?> witherRose = write(output, "abyss_wither_rose", witherRoseRule);
        CompletableFuture<?> yin = write(output, "yin_abyss_energy", yinRule);
        CompletableFuture<?> yang = write(output, "yang_abyss_energy", yangRule);
        return CompletableFuture.allOf(witherRose, yin, yang);
    }

    @Override
    public String getName() {
        return "ProjectK Abyss Energy Spawn Rules";
    }

    private CompletableFuture<?> write(CachedOutput output, String id, AbyssEnergySpawnRule rule) {
        JsonElement json = AbyssEnergySpawnRule.CODEC.encodeStart(JsonOps.INSTANCE, rule).getOrThrow();
        Path path = pathProvider.json(Id.id(id));
        return DataProvider.saveStable(output, json, path);
    }
}
