package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record AbyssEnergyData(ResourceLocation energyId, long amount) {
    public static final Codec<AbyssEnergyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("energy_id").forGetter(AbyssEnergyData::energyId),
            Codec.LONG.fieldOf("amount").forGetter(AbyssEnergyData::amount)
    ).apply(instance, AbyssEnergyData::new));

    public static final StreamCodec<ByteBuf, AbyssEnergyData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            AbyssEnergyData::energyId,
            ByteBufCodecs.VAR_LONG,
            AbyssEnergyData::amount,
            AbyssEnergyData::new
    );

    public static void applyToStack(ItemStack stack, ResourceLocation energyId, long amount) {
        if (amount < 0) {
            if (stack != null) {
                stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            }
            return;
        }
        if (stack == null || energyId == null) {
            return;
        }
        stack.set(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get(), new AbyssEnergyData(energyId, amount));
    }
}
