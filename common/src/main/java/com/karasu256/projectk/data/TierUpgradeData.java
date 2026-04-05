package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TierUpgradeData(int amount) {
    public static final Codec<TierUpgradeData> CODEC = Codec.INT
            .xmap(TierUpgradeData::new, TierUpgradeData::amount);

    public static final StreamCodec<ByteBuf, TierUpgradeData> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(TierUpgradeData::new, TierUpgradeData::amount);
}
