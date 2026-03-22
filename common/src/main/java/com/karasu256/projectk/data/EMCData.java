package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Optional;

public record EMCData(BigInteger emc) {
    public static final Codec<EMCData> CODEC = Codec.STRING.comapFlatMap(s -> {
        try {
            return DataResult.success(new BigInteger(s));
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Not a valid BigInteger: " + s);
        }
    }, BigInteger::toString).xmap(EMCData::new, EMCData::emc);

    public static final StreamCodec<ByteBuf, EMCData> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(BigInteger::new, BigInteger::toString)
            .map(EMCData::new, EMCData::emc);

    @NotNull
    public static Optional<EMCData> of(int value){
        return value == 0 ? Optional.empty() : Optional.of(new EMCData(BigInteger.valueOf(value)));
    }
}
