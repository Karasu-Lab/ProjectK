package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.item.ProjectKItems;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CommonItemModelProvider {

    public interface ItemGenerator {
        void simpleItem(RegistrySupplier<Item> item);
    }

    public static void generate(@NotNull ItemGenerator generator) {
        generator.simpleItem(ProjectKItems.KARASIUM);
        generator.simpleItem(ProjectKItems.RAW_KARASIUM);
        generator.simpleItem(ProjectKItems.KARASIUM_DUST);
        generator.simpleItem(ProjectKItems.WITHER_BONE);
        generator.simpleItem(ProjectKItems.ABYSS_INGOT);
        generator.simpleItem(ProjectKItems.YIN_ABYSS_INGOT);
        generator.simpleItem(ProjectKItems.YANG_ABYSS_INGOT);
        generator.simpleItem(ProjectKItems.ABYSS_WRENCH);
        generator.simpleItem(ProjectKItems.BUCKET_OF_ABYSS_ENERGY);
        generator.simpleItem(ProjectKItems.BUCKET_OF_YIN_ABYSS_ENERGY);
        generator.simpleItem(ProjectKItems.BUCKET_OF_YANG_ABYSS_ENERGY);
    }
}
