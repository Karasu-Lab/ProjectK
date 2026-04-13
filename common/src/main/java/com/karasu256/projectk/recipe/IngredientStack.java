package com.karasu256.projectk.recipe;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;
import java.util.List;

public record IngredientStack(Ingredient ingredient, int count, List<AbyssEnergyData> requiredEnergies,
                              int minDistinctEnergies, long minAmountPerEnergy) {
    public static final MapCodec<IngredientStack> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IngredientStack::ingredient),
                    Codec.intRange(1, Integer.MAX_VALUE).fieldOf("count").forGetter(IngredientStack::count),
                    AbyssEnergyData.CODEC.listOf().optionalFieldOf("required_energies", Collections.emptyList())
                            .forGetter(IngredientStack::requiredEnergies),
                    Codec.INT.optionalFieldOf("min_distinct_energies", 0).forGetter(IngredientStack::minDistinctEnergies),
                    Codec.LONG.optionalFieldOf("min_amount_per_energy", 0L).forGetter(IngredientStack::minAmountPerEnergy))
            .apply(builder, IngredientStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, IngredientStack::ingredient, ByteBufCodecs.VAR_INT,
            IngredientStack::count, AbyssEnergyData.STREAM_CODEC.apply(ByteBufCodecs.list()),
            IngredientStack::requiredEnergies, ByteBufCodecs.VAR_INT, IngredientStack::minDistinctEnergies,
            ByteBufCodecs.VAR_LONG, IngredientStack::minAmountPerEnergy, IngredientStack::new);

    public IngredientStack(Ingredient ingredient, int count) {
        this(ingredient, count, Collections.emptyList(), 0, 0L);
    }

    public IngredientStack(Ingredient ingredient, int count, List<AbyssEnergyData> requiredEnergies) {
        this(ingredient, count, requiredEnergies, 0, 0L);
    }

    public boolean test(ItemStack stack) {
        if (!ingredient.test(stack) || stack.getCount() < count) {
            return false;
        }
        if (!requiredEnergies.isEmpty()) {
            List<AbyssEnergyData> providedEnergies = AbyssEnergyData.readEnergyList(stack);
            for (AbyssEnergyData required : requiredEnergies) {
                boolean found = false;
                for (AbyssEnergyData available : providedEnergies) {
                    if (available.energyId()
                            .equals(required.energyId()) && available.amountOrZero() >= required.amountOrZero()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        if (minDistinctEnergies > 0) {
            List<AbyssEnergyData> providedEnergies = AbyssEnergyData.readEnergyList(stack);
            int count = 0;
            for (AbyssEnergyData available : providedEnergies) {
                if (available.amountOrZero() >= minAmountPerEnergy) {
                    count++;
                }
            }
            return count >= minDistinctEnergies;
        }
        return true;
    }
}
