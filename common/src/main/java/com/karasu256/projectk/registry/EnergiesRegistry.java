package com.karasu256.projectk.registry;

import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class EnergiesRegistry {
    private static final DeferredRegister<IEnergy> ENERGIES = DeferredRegister.create(MOD_ID, KarasunikiRegistries.ENERGY);

    public static final RegistrySupplier<IEnergy> ABYSS_ENERGY = ENERGIES.register(Id.id("abyss_energy"), () -> new AbyssEnergy(0L));

    public static void register() {
        ENERGIES.register();
    }
}
