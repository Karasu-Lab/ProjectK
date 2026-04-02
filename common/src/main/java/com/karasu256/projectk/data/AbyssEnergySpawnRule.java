package com.karasu256.projectk.data;

import com.karasu256.projectk.data.spawn.BlockCondition;
import com.karasu256.projectk.data.spawn.EnergyAmountRule;
import com.karasu256.projectk.data.spawn.MobCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record AbyssEnergySpawnRule(ResourceLocation energyId, MobCondition mob, BlockCondition block,
                                   EnergyAmountRule amount) {
    public static final MapCodec<AbyssEnergySpawnRule> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("energy_id").forGetter(AbyssEnergySpawnRule::energyId),
            MobCondition.CODEC.fieldOf("mob").forGetter(AbyssEnergySpawnRule::mob),
            BlockCondition.CODEC.optionalFieldOf("block").forGetter(rule -> Optional.ofNullable(rule.block())),
            EnergyAmountRule.CODEC.fieldOf("amount").forGetter(AbyssEnergySpawnRule::amount)
    ).apply(instance, (energyId, mob, blockOpt, amount) -> new AbyssEnergySpawnRule(energyId, mob, blockOpt.orElse(BlockCondition.ANY), amount)));

    public static final Codec<AbyssEnergySpawnRule> CODEC = MAP_CODEC.codec();
}
