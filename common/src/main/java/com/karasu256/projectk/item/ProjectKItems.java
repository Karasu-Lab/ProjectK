package com.karasu256.projectk.item;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.fluid.AbyssEnergyFluidConversion;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.item.custom.AbyssEnergyItem;
import com.karasu256.projectk.item.custom.AbyssWrenchItem;
import com.karasu256.projectk.item.custom.ProjectKItem;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static com.karasu256.projectk.registry.ItemsRegistry.item;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 2)
public class ProjectKItems implements IKRegistryInitializerTarget {
    public static RegistrySupplier<Item> KARASIUM = item("karasium", () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)));
    public static RegistrySupplier<Item> RAW_KARASIUM = item("raw_karasium", () -> new ProjectKItem(new ProjectKItem.Properties()));
    public static RegistrySupplier<Item> KARASIUM_DUST = item("karasium_dust", () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)));
    public static RegistrySupplier<Item> WITHER_BONE = item("wither_bone", () -> new ProjectKItem(new ProjectKItem.Properties()));
    public static RegistrySupplier<Item> ABYSS_INGOT = item("abyss_ingot", () -> new AbyssEnergyItem(new ProjectKItem.Properties()));
    public static RegistrySupplier<Item> YIN_ABYSS_INGOT = item("yin_abyss_ingot", () -> new AbyssEnergyItem(new ProjectKItem.Properties().abyssEnergy(ProjectKEnergies.YIN_ABYSS_ENERGY, 500)));
    public static RegistrySupplier<Item> YANG_ABYSS_INGOT = item("yang_abyss_ingot", () -> new AbyssEnergyItem(new ProjectKItem.Properties().abyssEnergy(ProjectKEnergies.YANG_ABYSS_ENERGY, 500)));
    public static RegistrySupplier<Item> ABYSS_WRENCH = item("abyss_wrench", () -> new AbyssWrenchItem(new ProjectKItem.Properties()));
    public static RegistrySupplier<Item> BUCKET_OF_ABYSS_ENERGY = item("bucket_of_abyss_energy", () -> {
        ProjectKItem.Properties properties = new ProjectKItem.Properties().abyssEnergy(ProjectKEnergies.ABYSS_ENERGY, AbyssEnergyFluidConversion.DEFAULT_ENERGY_PER_BUCKET);
        properties.craftRemainder(Items.BUCKET);
        properties.stacksTo(1);
        return new ArchitecturyBucketItem(ProjectKFluids.ABYSS_ENERGY, properties);
    });

    public static RegistrySupplier<Item> BUCKET_OF_YIN_ABYSS_ENERGY = item("bucket_of_yin_abyss_energy", () -> {
        ProjectKItem.Properties properties = new ProjectKItem.Properties().abyssEnergy(ProjectKEnergies.YIN_ABYSS_ENERGY, AbyssEnergyFluidConversion.DEFAULT_ENERGY_PER_BUCKET);
        properties.craftRemainder(Items.BUCKET);
        properties.stacksTo(1);
        return new ArchitecturyBucketItem(ProjectKFluids.YIN_ABYSS_ENERGY, properties);
    });

    public static RegistrySupplier<Item> BUCKET_OF_YANG_ABYSS_ENERGY = item("bucket_of_yang_abyss_energy", () -> {
        ProjectKItem.Properties properties = new ProjectKItem.Properties().abyssEnergy(ProjectKEnergies.YANG_ABYSS_ENERGY, AbyssEnergyFluidConversion.DEFAULT_ENERGY_PER_BUCKET);
        properties.craftRemainder(Items.BUCKET);
        properties.stacksTo(1);
        return new ArchitecturyBucketItem(ProjectKFluids.YANG_ABYSS_ENERGY, properties);
    });

    public static void init() {
    }
}
