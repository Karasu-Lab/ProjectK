package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.machine.IMachineCapacity;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 7)
public class MachineCapacityRegistry implements IKRegistryTarget {
    public static final ResourceKey<Registry<IMachineCapacity>> KEY = ResourceKey.createRegistryKey(
            Id.id("machine_capacities"));
    public static final Registrar<IMachineCapacity> REGISTRAR = RegistrarManager.get(ProjectK.MOD_ID)
            .<IMachineCapacity>builder(Id.id("machine_capacities")).build();
    private static final DeferredRegister<IMachineCapacity> CAPACITIES = DeferredRegister.create(ProjectK.MOD_ID, KEY);

    public static void register() {
        CAPACITIES.register();
    }

    public static RegistrySupplier<IMachineCapacity> register(String name, Supplier<IMachineCapacity> supplier) {
        return CAPACITIES.register(name, supplier);
    }

    public static long resolve(ResourceLocation key, int tier) {
        IMachineCapacity capacity = REGISTRAR.get(key);
        return capacity != null ? capacity.getCapacity(tier) : -1L;
    }

    public static long resolve(BlockEntityType<?> type, int tier) {
        ResourceLocation key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
        return key != null ? resolve(key, tier) : -1L;
    }

    public static boolean isRegistered(ResourceLocation key) {
        return REGISTRAR.contains(key);
    }

    public static boolean isRegistered(BlockEntityType<?> type) {
        ResourceLocation key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
        return key != null && isRegistered(key);
    }
}
