package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KDataComponentRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 0)
public class DataComponentTypesRegistry implements IKRegistryTarget {
    public static void register() {
        KDataComponentRegistry.register(ProjectK.MOD_ID);
    }

    public static <T> RegistrySupplier<DataComponentType<T>> dataComponent(String id, Supplier<DataComponentType<T>> dataComponent) {
        return KDataComponentRegistry.dataComponent(ProjectK.MOD_ID, id, dataComponent);
    }
}
