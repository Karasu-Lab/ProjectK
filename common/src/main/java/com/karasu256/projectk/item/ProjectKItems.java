package com.karasu256.projectk.item;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.fluid.AbyssEnergyFluidConversion;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.item.custom.AbyssEnergyItem;
import com.karasu256.projectk.item.custom.AbyssWrenchItem;
import com.karasu256.projectk.item.custom.ProjectKItem;
import com.karasu256.projectk.registry.CreativeTabsRegistry;
import com.karasu256.projectk.registry.EnergyAutoRegistry;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

import static com.karasu256.projectk.registry.ItemsRegistry.item;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 2)
public class ProjectKItems implements IKRegistryInitializerTarget {
    public static final Map<ResourceLocation, RegistrySupplier<Item>> ABYSS_ENERGY_BUCKETS = EnergyAutoRegistry.mapByEnergy(
            definition -> "bucket_of_" + definition.idPath(),
            (definition, id, map) -> {
                map.put(definition.id(), item(id, () -> {
                    ProjectKItem.Properties properties = new ProjectKItem.Properties().abyssEnergy(definition.id(), AbyssEnergyFluidConversion.DEFAULT_ENERGY_PER_BUCKET);
                    properties.craftRemainder(Items.BUCKET);
                    properties.stacksTo(1);
                    return new ArchitecturyBucketItem(ProjectKFluids.getSource(definition.id()), properties);
                }));
            }
    );
    public static final RegistrySupplier<Item> BUCKET_OF_ABYSS_ENERGY = getBucket(ProjectKEnergies.ABYSS.id());
    public static final RegistrySupplier<Item> BUCKET_OF_YIN_ABYSS_ENERGY = getBucket(ProjectKEnergies.YIN.id());
    public static final RegistrySupplier<Item> BUCKET_OF_YANG_ABYSS_ENERGY = getBucket(ProjectKEnergies.YANG.id());
    public static RegistrySupplier<Item> KARASIUM = item("karasium", () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)));
    public static RegistrySupplier<Item> RAW_KARASIUM = item("raw_karasium", () -> new ProjectKItem(new ProjectKItem.Properties()));
    public static RegistrySupplier<Item> KARASIUM_DUST = item("karasium_dust", () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)));
    public static RegistrySupplier<Item> WITHER_BONE = item("wither_bone", () -> new ProjectKItem(new ProjectKItem.Properties()));
    public static RegistrySupplier<Item> ABYSS_INGOT = item("abyss_ingot", () -> new AbyssEnergyItem(new ProjectKItem.Properties()), true);
    public static RegistrySupplier<Item> ABYSS_WRENCH = item("abyss_wrench", () -> new AbyssWrenchItem(new ProjectKItem.Properties()));

    static {
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            CreativeTabsRegistry.tabStack(() -> buildAbyssIngotVariant(definition));
        }
    }

    public static void init() {
    }

    public static RegistrySupplier<Item> getBucket(ResourceLocation energyId) {
        return ABYSS_ENERGY_BUCKETS.get(energyId);
    }

    private static ItemStack buildAbyssIngotVariant(ProjectKEnergies.EnergyDefinition definition) {
        ItemStack stack = new ItemStack(ABYSS_INGOT.get());
        AbyssEnergyData.applyToStack(stack, definition.id(), definition.defaultAmount());
        return stack;
    }
}
