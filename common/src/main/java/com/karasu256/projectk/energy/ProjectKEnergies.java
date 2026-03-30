package com.karasu256.projectk.energy;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;

import com.karasu256.projectk.registry.EnergiesRegistry;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 2)
public class ProjectKEnergies implements IKRegistryInitializerTarget {
    public static final RegistrySupplier<IEnergy> ABYSS_ENERGY = EnergiesRegistry.registerEnergy("abyss_energy", () -> new AbyssEnergy(Id.id("abyss_energy"), 0L));
    public static final RegistrySupplier<IEnergy> YIN_ABYSS_ENERGY = EnergiesRegistry.registerEnergy("yin_abyss_energy", () -> new AbyssEnergy(Id.id("yin_abyss_energy"), 0L));
    public static final RegistrySupplier<IEnergy> YANG_ABYSS_ENERGY = EnergiesRegistry.registerEnergy("yang_abyss_energy", () -> new AbyssEnergy(Id.id("yang_abyss_energy"), 0L));

    public static void init() {
    }
}
