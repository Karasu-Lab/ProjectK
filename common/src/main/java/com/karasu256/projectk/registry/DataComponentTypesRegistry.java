package com.karasu256.projectk.registry;

import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class DataComponentTypesRegistry {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static void register() {
        DATA_COMPONENT_TYPES.register();
    }

    public static <T> RegistrySupplier<DataComponentType<T>> dataComponent(String id, Supplier<DataComponentType<T>> dataComponent) {
        return DATA_COMPONENT_TYPES.register(Id.id(id), dataComponent);
    }
}
