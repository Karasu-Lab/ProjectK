package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EnergyCapacityData(long capacity) {
    public static final Codec<EnergyCapacityData> CODEC = Codec.LONG
            .xmap(EnergyCapacityData::new, EnergyCapacityData::capacity);

    public static final StreamCodec<ByteBuf, EnergyCapacityData> STREAM_CODEC = ByteBufCodecs.VAR_LONG
            .map(EnergyCapacityData::new, EnergyCapacityData::capacity);
}
