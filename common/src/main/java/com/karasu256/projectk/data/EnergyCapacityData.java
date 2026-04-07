package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record EnergyCapacityData(Optional<Long> capacity) {
    public static final Codec<EnergyCapacityData> CODEC = Codec.LONG.optionalFieldOf("capacity")
            .xmap(EnergyCapacityData::new, EnergyCapacityData::capacity).codec();

    public static final StreamCodec<ByteBuf, EnergyCapacityData> STREAM_CODEC = ByteBufCodecs.optional(
            ByteBufCodecs.VAR_LONG).map(EnergyCapacityData::new, EnergyCapacityData::capacity);

    public static EnergyCapacityData infinite() {
        return new EnergyCapacityData(Optional.empty());
    }

    public static EnergyCapacityData of(long capacity) {
        return new EnergyCapacityData(Optional.of(capacity));
    }

    public boolean isInfinite() {
        return capacity.isEmpty();
    }

    public Long get() {
        return capacity.orElse(null);
    }
}
