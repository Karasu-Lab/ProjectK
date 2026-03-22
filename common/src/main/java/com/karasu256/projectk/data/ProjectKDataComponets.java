package com.karasu256.projectk.data;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;

import static com.karasu256.projectk.registry.DataComponentTypesRegistry.dataComponent;


public class ProjectKDataComponets {
    public static RegistrySupplier<DataComponentType<EMCData>> EMC_DATACOMPONENT_TYPE = dataComponent("emc",
            () -> DataComponentType.<EMCData>builder()
                    .persistent(EMCData.CODEC)
                    .networkSynchronized(EMCData.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static void init() {
    }
}
