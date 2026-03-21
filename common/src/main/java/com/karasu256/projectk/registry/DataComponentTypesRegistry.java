package com.karasu256.projectk.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class DataComponentTypesRegistry {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static void register() {
        DATA_COMPONENT_TYPES.register();
    }
}
