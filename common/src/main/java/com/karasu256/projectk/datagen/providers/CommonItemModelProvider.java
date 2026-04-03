package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.ItemsRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CommonItemModelProvider {

    public static void generate(@NotNull ItemGenerator generator) {
        generator.simpleItem(ProjectKItems.KARASIUM);
        generator.simpleItem(ProjectKItems.RAW_KARASIUM);
        generator.simpleItem(ProjectKItems.KARASIUM_DUST);
        generator.simpleItem(ProjectKItems.WITHER_BONE);
        if (!ItemsRegistry.getEnergySuffixItems().contains(ProjectKItems.ABYSS_INGOT.getId())) {
            generator.simpleItem(ProjectKItems.ABYSS_INGOT);
        }
        generator.simpleItem(ProjectKItems.ABYSS_WRENCH);
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            generator.simpleItem(ProjectKItems.getBucket(definition.id()));
        }
    }

    public interface ItemGenerator {
        void simpleItem(RegistrySupplier<Item> item);
    }
}
