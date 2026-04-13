package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.machine.IMachineCapacity;
import com.karasu256.projectk.data.AbyssLaserEmitterTierManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 8)
public class ProjectKMachineCapacities implements IKRegistryInitializerTarget {

    public static void init() {
    }

    private static IMachineCapacity tiered(long base) {
        return tier -> {
            if (tier <= 1)
                return base;
            return (long) (base * Math.pow(2.5, tier - 1));
        };
    }

    public static final RegistrySupplier<IMachineCapacity> ABYSS_PORTAL = MachineCapacityRegistry.register(
            "abyss_portal", () -> tiered(10_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_LASER_EMITTER = MachineCapacityRegistry.register(
            "abyss_laser_emitter", () -> tier -> AbyssLaserEmitterTierManager.getTier(tier).capacity());

    public static final RegistrySupplier<IMachineCapacity> ABYSS_CORE = MachineCapacityRegistry.register("abyss_core",
            () -> tiered(5_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_GENERATOR = MachineCapacityRegistry.register(
            "abyss_generator", () -> tiered(10_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_MAGIC_TABLE = MachineCapacityRegistry.register(
            "abyss_magic_table", () -> tiered(10_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_ALCHEMY_BLEND_MACHINE = MachineCapacityRegistry.register(
            "abyss_alchemy_blend_machine", () -> tiered(10_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_ENCHANTER = MachineCapacityRegistry.register(
            "abyss_enchanter", () -> tiered(30_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_CHARGER = MachineCapacityRegistry.register(
            "abyss_charger", () -> tiered(30_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_STORAGE = MachineCapacityRegistry.register(
            "abyss_storage", () -> tiered(30_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_ENERGY_CABLE = MachineCapacityRegistry.register(
            "abyss_energy_cable", () -> tiered(10_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_SYNTHESIZER = MachineCapacityRegistry.register(
            "abyss_synthesizer", () -> tiered(100_000L));

    public static final RegistrySupplier<IMachineCapacity> ABYSS_ABSORPTION_PRISM = MachineCapacityRegistry.register(
            "abyss_absorption_prism", () -> tiered(1_000L));

    public static final RegistrySupplier<IMachineCapacity> NONE = MachineCapacityRegistry.register("none",
            () -> tier -> 0L);

    public static final RegistrySupplier<IMachineCapacity> INFINITIE = MachineCapacityRegistry.register("infinitie",
            () -> tier -> Long.MAX_VALUE);
}
