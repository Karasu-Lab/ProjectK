package com.karasu256.projectk.energy;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.energy.PKMaterials;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.registry.EnergiesRegistry;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 2)
public class ProjectKEnergies implements IKRegistryInitializerTarget {
    public static final long INFINITE_THRESHOLD = 1_000_000_000_000_000_000L;
    public static final ResourceLocation BASE_ID = Id.id("abyss_energy");
    private static final Map<ResourceLocation, RegistrySupplier<IEnergy>> ENERGIES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, EnergyDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Integer> MODEL_INDICES = new LinkedHashMap<>();
    private static final Map<PKMaterials, EnergyDefinition> MATERIAL_TO_DEFINITION = new EnumMap<>(PKMaterials.class);
    private static final Map<PKMaterials, RegistrySupplier<IEnergy>> MATERIAL_TO_ENERGY = new EnumMap<>(
            PKMaterials.class);
    private static final float MODEL_PREDICATE_SCALE = 1000.0f;

    static {
        for (PKMaterials material : PKMaterials.values()) {
            registerFromMaterial(material, 500L);
        }
    }

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

    public static ResourceLocation getEnergyIdByModelIndex(int index) {
        if (index <= 0) {
            return null;
        }
        for (Map.Entry<ResourceLocation, Integer> entry : MODEL_INDICES.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static ResourceLocation getEnergyIdByKind(EnergyKind kind) {
        return DEFINITIONS.values().stream()
                .filter(d -> d.kind() == kind)
                .map(EnergyDefinition::id)
                .findFirst()
                .orElseGet(() -> DEFINITIONS.values().stream()
                        .filter(d -> d.kind() == EnergyKind.NEUTRAL)
                        .map(EnergyDefinition::id)
                        .findFirst()
                        .orElse(null));
    }

    public static boolean isAbyssEnergyId(ResourceLocation id) {
        return id != null && ENERGIES.containsKey(id);
    }

    public static boolean isInfinite(long amount) {
        return amount >= INFINITE_THRESHOLD || amount < 0;
    }

    public static boolean isInfinite(long amount, Long capacity) {
        if (isInfinite(amount)) {
            return true;
        }
        return capacity != null && isInfinite(capacity);
    }

    public static boolean isInfinite(long amount, EnergyCapacityData capacityData) {
        if (capacityData == null) {
            return isInfinite(amount);
        }
        if (capacityData.isInfinite()) {
            return true;
        }
        return isInfinite(amount, capacityData.get());
    }

    public static RegistrySupplier<IEnergy> energy(EnergyDefinition definition) {
        return ENERGIES.get(definition.id());
    }

    private static EnergyDefinition getByMaterial(PKMaterials material) {
        return MATERIAL_TO_DEFINITION.get(material);
    }

    private static EnergyDefinition registerFromMaterial(PKMaterials material, long defaultAmount) {
        EnergyDefinition definition = registerDefinition(material.energyIdPath(), material.enName(), material.jaName(),
                material.kind(), defaultAmount, material.color());
        MATERIAL_TO_DEFINITION.put(material, definition);
        MATERIAL_TO_ENERGY.put(material, ENERGIES.get(definition.id()));
        return definition;
    }

    public static EnergyDefinition registerDefinition(String idPath, String enName, String jaName, EnergyKind kind, long defaultAmount, int color) {
        ResourceLocation id = Id.id(idPath);
        RegistrySupplier<IEnergy> energy = EnergiesRegistry.registerEnergy(idPath, () -> new AbyssEnergy(id, 0L));
        EnergyDefinition definition = new EnergyDefinition(id, enName, jaName, kind, defaultAmount, color);
        ENERGIES.put(id, energy);
        DEFINITIONS.put(id, definition);
        MODEL_INDICES.put(id, MODEL_INDICES.size() + 1);
        return definition;
    }

    public enum EnergyKind {
        NEUTRAL, YIN, YANG,
    }

    public record EnergyDefinition(ResourceLocation id, String enName, String jaName, EnergyKind kind,
                                   long defaultAmount, int color) {
        public String idPath() {
            return id.getPath();
        }

        public boolean isBase() {
            return id.equals(BASE_ID);
        }

        public String getTranslationKey() {
            return "energy." + id.getNamespace() + "." + id.getPath();
        }

        public Component getDisplayName() {
            return Component.translatable(getTranslationKey());
        }
    }
}
