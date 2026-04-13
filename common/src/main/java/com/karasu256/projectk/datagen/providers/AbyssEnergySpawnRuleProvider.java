package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.data.AbyssEnergySpawnRule;
import com.karasu256.projectk.data.spawn.BlockCondition;
import com.karasu256.projectk.data.spawn.EnergyAmountRule;
import com.karasu256.projectk.data.spawn.MobCondition;
import com.karasu256.projectk.api.datagen.impl.AbstractAbyssEnergySpawnRuleProvider;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AbyssEnergySpawnRuleProvider extends AbstractAbyssEnergySpawnRuleProvider {
    public AbyssEnergySpawnRuleProvider(PackOutput output) {
        super(output, "abyss_energy_spawns");
    }

    @Override
    public CompletableFuture<?> runDataGen(CachedOutput cachedOutput) {
        AbyssEnergySpawnRule witherRoseRule = new AbyssEnergySpawnRule(ProjectKEnergies.ABYSS.id(), new MobCondition(),
                new BlockCondition(5, List.of(), List.of(ResourceLocation.withDefaultNamespace("wither_rose"))),
                new EnergyAmountRule(EnergyAmountRule.AmountType.BUILT_IN, 0L));
        AbyssEnergySpawnRule yinRule = new AbyssEnergySpawnRule(ProjectKEnergies.YIN.id(),
                new MobCondition(List.of(MobCategory.MONSTER), List.of(), List.of(), List.of()), BlockCondition.ANY,
                new EnergyAmountRule(EnergyAmountRule.AmountType.BUILT_IN, 0L));

        AbyssEnergySpawnRule yangRule = new AbyssEnergySpawnRule(ProjectKEnergies.YANG.id(), new MobCondition(
                List.of(MobCategory.CREATURE, MobCategory.WATER_CREATURE, MobCategory.AXOLOTLS,
                        MobCategory.UNDERGROUND_WATER_CREATURE), List.of(), List.of(), List.of()), BlockCondition.ANY,
                new EnergyAmountRule(EnergyAmountRule.AmountType.BUILT_IN, 0L));

        addData("abyss_wither_rose", witherRoseRule);
        addData("yin_abyss_energy", yinRule);
        addData("yang_abyss_energy", yangRule);

        return super.runDataGen(cachedOutput);
    }
}
