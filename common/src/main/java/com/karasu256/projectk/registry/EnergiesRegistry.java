package com.karasu256.projectk.registry;

import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class EnergiesRegistry {
    private static final DeferredRegister<IEnergy> ENERGIES = DeferredRegister.create(MOD_ID, KarasunikiRegistries.ENERGY_REGISTRY_KEY);

    public static final RegistrySupplier<IEnergy> ABYSS_ENERGY = ENERGIES.register(Id.id("abyss_energy"), () -> new AbyssEnergy(Id.id("abyss_energy"), 0L));
    public static final RegistrySupplier<IEnergy> YIN_ABYSS_ENERGY = ENERGIES.register(Id.id("yin_abyss_energy"), () -> new AbyssEnergy(Id.id("yin_abyss_energy"), 0L));
    public static final RegistrySupplier<IEnergy> YANG_ABYSS_ENERGY = ENERGIES.register(Id.id("yang_abyss_energy"), () -> new AbyssEnergy(Id.id("yang_abyss_energy"), 0L));

    public static void register() {
        ENERGIES.register();
    }

    public static Iterable<RegistrySupplier<IEnergy>> getEntries() {
        return ENERGIES;
    }
}
