package com.karasu256.projectk.data;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.core.component.DataComponentType;

import static com.karasu256.projectk.registry.DataComponentTypesRegistry.dataComponent;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 1)
public class ProjectKDataComponets implements IKRegistryInitializerTarget {
    public static RegistrySupplier<DataComponentType<EMCData>> EMC_DATACOMPONENT_TYPE = dataComponent("emc",
            () -> DataComponentType.<EMCData>builder()
                    .persistent(EMCData.CODEC)
                    .networkSynchronized(EMCData.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static RegistrySupplier<DataComponentType<EnergyCapacityData>> ENERGY_CAPACITY_DATA_COMPONENT_TYPE = dataComponent("energy_capacity",
            () -> DataComponentType.<EnergyCapacityData>builder()
                    .persistent(EnergyCapacityData.CODEC)
                    .networkSynchronized(EnergyCapacityData.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static RegistrySupplier<DataComponentType<AbyssEnergyData>> ABYSS_ENERGY_DATA_COMPONENT_TYPE = dataComponent("abyss_energy",
            () -> DataComponentType.<AbyssEnergyData>builder()
                    .persistent(AbyssEnergyData.CODEC)
                    .networkSynchronized(AbyssEnergyData.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static RegistrySupplier<DataComponentType<AbyssWrenchBehaviorData>> ABYSS_WRENCH_BEHAVIOR_DATA_COMPONENT_TYPE = dataComponent("abyss_wrench_behavior",
            () -> DataComponentType.<AbyssWrenchBehaviorData>builder()
                    .persistent(AbyssWrenchBehaviorData.CODEC)
                    .networkSynchronized(AbyssWrenchBehaviorData.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static void init() {
    }
}
