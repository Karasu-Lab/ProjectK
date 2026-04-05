package com.karasu256.projectk.data;

import com.karasu256.projectk.ProjectK;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.RegistrySupplier;
import io.netty.buffer.ByteBuf;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import static com.karasu256.projectk.registry.DataComponentTypesRegistry.dataComponent;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 1)
public record EnergyCapacityData(long capacity) implements IKRegistryInitializerTarget {
    public static final Codec<EnergyCapacityData> CODEC = Codec.LONG
            .xmap(EnergyCapacityData::new, EnergyCapacityData::capacity);

    public static final StreamCodec<ByteBuf, EnergyCapacityData> STREAM_CODEC = ByteBufCodecs.VAR_LONG
            .map(EnergyCapacityData::new, EnergyCapacityData::capacity);

    public static final RegistrySupplier<DataComponentType<EnergyCapacityData>> ENERGY_CAPACITY_DATA_COMPONENT_TYPE = dataComponent("energy_capacity",
            () -> DataComponentType.<EnergyCapacityData>builder()
                    .persistent(EnergyCapacityData.CODEC)
                    .networkSynchronized(EnergyCapacityData.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static void init() {
    }
}
