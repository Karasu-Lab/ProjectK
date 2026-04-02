package com.karasu256.projectk.energy;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;

import com.karasu256.projectk.registry.EnergiesRegistry;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 2)
public class ProjectKEnergies implements IKRegistryInitializerTarget {
    private static final Map<ResourceLocation, RegistrySupplier<IEnergy>> ENERGIES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, EnergyDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Integer> MODEL_INDICES = new LinkedHashMap<>();
    private static final float MODEL_PREDICATE_SCALE = 1000.0f;

    public static final EnergyDefinition ABYSS = registerDefinition("abyss_energy", "Abyss", "深淵", EnergyKind.NEUTRAL, 500L);
    public static final EnergyDefinition YIN = registerDefinition("yin_abyss_energy", "§5Yin", "§5陰", EnergyKind.YIN, 500L);
    public static final EnergyDefinition YANG = registerDefinition("yang_abyss_energy", "§dYang", "§d陽", EnergyKind.YANG, 500L);

    public static final RegistrySupplier<IEnergy> ABYSS_ENERGY = energy(ABYSS);
    public static final RegistrySupplier<IEnergy> YIN_ABYSS_ENERGY = energy(YIN);
    public static final RegistrySupplier<IEnergy> YANG_ABYSS_ENERGY = energy(YANG);

    public static void init() {
    }

    public static List<EnergyDefinition> getDefinitions() {
        return List.copyOf(DEFINITIONS.values());
    }

    public static Iterable<RegistrySupplier<IEnergy>> getEntries() {
        return Collections.unmodifiableCollection(ENERGIES.values());
    }

    public static Optional<EnergyDefinition> getDefinition(ResourceLocation id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }

    public static int getModelIndex(ResourceLocation id) {
        Integer index = MODEL_INDICES.get(id);
        return index == null ? 0 : index;
    }

    public static float getModelPredicateValue(ResourceLocation id) {
        return getModelIndex(id) / MODEL_PREDICATE_SCALE;
    }

    public static ResourceLocation getEnergyIdByKind(EnergyKind kind) {
        for (EnergyDefinition definition : DEFINITIONS.values()) {
            if (definition.kind() == kind) {
                return definition.id();
            }
        }
        return ABYSS.id();
    }

    public static boolean isAbyssEnergyId(ResourceLocation id) {
        return id != null && ENERGIES.containsKey(id);
    }

    public static RegistrySupplier<IEnergy> energy(EnergyDefinition definition) {
        return ENERGIES.get(definition.id());
    }

    private static EnergyDefinition registerDefinition(String idPath, String enName, String jaName, EnergyKind kind, long defaultAmount) {
        ResourceLocation id = Id.id(idPath);
        RegistrySupplier<IEnergy> energy = EnergiesRegistry.registerEnergy(idPath, () -> new AbyssEnergy(id, 0L));
        EnergyDefinition definition = new EnergyDefinition(id, enName, jaName, kind, defaultAmount);
        ENERGIES.put(id, energy);
        DEFINITIONS.put(id, definition);
        MODEL_INDICES.put(id, MODEL_INDICES.size() + 1);
        return definition;
    }

    public enum EnergyKind {
        NEUTRAL,
        YIN,
        YANG,
    }

    public record EnergyDefinition(ResourceLocation id, String enName, String jaName, EnergyKind kind, long defaultAmount) {
        public String idPath() {
            return id.getPath();
        }
    }
}
