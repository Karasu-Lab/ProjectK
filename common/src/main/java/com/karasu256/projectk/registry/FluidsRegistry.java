package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 2)
public class FluidsRegistry implements IKRegistryTarget {
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ProjectK.MOD_ID, Registries.FLUID);

    public static void register() {
        FLUIDS.register();
    }

    public static <T extends Fluid> RegistrySupplier<T> fluid(String id, Supplier<T> fluid) {
        return FLUIDS.register(id, fluid);
    }
}
