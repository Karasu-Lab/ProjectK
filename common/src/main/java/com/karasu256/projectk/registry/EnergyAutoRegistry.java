package com.karasu256.projectk.registry;

import com.karasu256.projectk.energy.ProjectKEnergies;
import net.karasuniki.karasunikilib.utils.TriConsumer;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class EnergyAutoRegistry {
    private EnergyAutoRegistry() {
    }

    public static <V> Map<ResourceLocation, V> mapByEnergy(
            Function<ProjectKEnergies.EnergyDefinition, String> idFactory,
            TriConsumer<ProjectKEnergies.EnergyDefinition, String, Map<ResourceLocation, V>> registrar
    ) {
        Map<ResourceLocation, V> map = new LinkedHashMap<>();
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            String id = idFactory.apply(definition);
            registrar.accept(definition, id, map);
        }
        return map;
    }
}
