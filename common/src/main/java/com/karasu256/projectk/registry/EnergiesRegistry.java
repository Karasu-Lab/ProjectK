package com.karasu256.projectk.registry;

import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class EnergiesRegistry {
    private static final DeferredRegister<IEnergy> ENERGIES = DeferredRegister.create(MOD_ID, KarasunikiRegistries.ENERGY_REGISTRY_KEY);
    private static final List<RegistrySupplier<IEnergy>> ENTRIES = new ArrayList<>();

    public static final RegistrySupplier<IEnergy> ABYSS_ENERGY = register("abyss_energy", () -> new AbyssEnergy(Id.id("abyss_energy"), 0L));
    public static final RegistrySupplier<IEnergy> YIN_ABYSS_ENERGY = register("yin_abyss_energy", () -> new AbyssEnergy(Id.id("yin_abyss_energy"), 0L));
    public static final RegistrySupplier<IEnergy> YANG_ABYSS_ENERGY = register("yang_abyss_energy", () -> new AbyssEnergy(Id.id("yang_abyss_energy"), 0L));

    public static void register() {
        ENERGIES.register();
    }

    private static RegistrySupplier<IEnergy> register(String name, Supplier<IEnergy> energySupplier) {
        RegistrySupplier<IEnergy> registered = ENERGIES.register(Id.id(name), energySupplier);
        ENTRIES.add(registered);
        return registered;
    }

    public static Iterable<RegistrySupplier<IEnergy>> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }
}
