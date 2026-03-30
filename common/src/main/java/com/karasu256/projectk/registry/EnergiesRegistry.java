package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 6)
public class EnergiesRegistry implements IKRegistryTarget {
    private static final DeferredRegister<IEnergy> ENERGIES = DeferredRegister.create(ProjectK.MOD_ID, KarasunikiRegistries.ENERGY_REGISTRY_KEY);
    private static final List<RegistrySupplier<IEnergy>> ENTRIES = new ArrayList<>();

    public static void register() {
        ENERGIES.register();
    }

    public static RegistrySupplier<IEnergy> registerEnergy(String name, Supplier<IEnergy> energySupplier) {
        RegistrySupplier<IEnergy> registered = ENERGIES.register(Id.id(name), energySupplier);
        ENTRIES.add(registered);
        return registered;
    }

    public static Iterable<RegistrySupplier<IEnergy>> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }
}
