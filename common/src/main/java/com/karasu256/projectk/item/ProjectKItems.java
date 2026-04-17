package com.karasu256.projectk.item;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.fluid.AbyssEnergyFluidConversion;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.data.TierUpgradeData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.item.custom.*;
import com.karasu256.projectk.registry.CreativeTabsRegistry;
import com.karasu256.projectk.registry.EnergyAutoRegistry;
import com.karasu256.projectk.registry.ItemsRegistry;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Map;

import static com.karasu256.projectk.registry.CreativeTabsRegistry.*;
import static com.karasu256.projectk.registry.ItemsRegistry.item;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 2)
public class ProjectKItems implements IKRegistryInitializerTarget {
    public static final Map<ResourceLocation, RegistrySupplier<Item>> ABYSS_ENERGY_BUCKETS = EnergyAutoRegistry.mapByEnergy(
            definition -> "bucket_of_" + definition.idPath(), (definition, id, map) -> {
                map.put(definition.id(), ItemsRegistry.item(id, () -> {
                    ProjectKItem.Properties properties = new ProjectKItem.Properties().abyssEnergy(definition.id(),
                            AbyssEnergyFluidConversion.DEFAULT_ENERGY_PER_BUCKET);
                    properties.craftRemainder(Items.BUCKET);
                    properties.stacksTo(1);
                    return new ArchitecturyBucketItem(ProjectKFluids.getSource(definition.id()), properties);
                }, FLUIDS));
            });

    public static final RegistrySupplier<Item> KARASIUM = item("karasium",
            () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)), INGOT);

    public static final RegistrySupplier<Item> RAW_KARASIUM = item("raw_karasium",
            () -> new ProjectKItem(new ProjectKItem.Properties()), INGOT);

    public static final RegistrySupplier<Item> KARASIUM_DUST = item("karasium_dust",
            () -> new ProjectKItem(new ProjectKItem.Properties().emc(128L)), INGOT);

    public static final RegistrySupplier<Item> WITHER_BONE = item("wither_bone",
            () -> new ProjectKItem(new ProjectKItem.Properties()), MATERIALS);

    public static final RegistrySupplier<Item> ABYSS_INGOT = item("abyss_ingot",
            () -> new AbyssEnergyItem(new ProjectKItem.Properties()), true, INGOT);

    public static final RegistrySupplier<Item> ABYSS_WRENCH = item("abyss_wrench", () -> {
        ProjectKItem.Properties properties = (ProjectKItem.Properties) new ProjectKItem.Properties().stacksTo(1);
        return new AbyssWrenchItem(properties);
    }, MATERIALS);

    public static final RegistrySupplier<Item> TIER_UPGRADE = item("tier_upgrade", () -> {
        ProjectKItem.Properties properties = new ProjectKItem.Properties();
        properties.component(ProjectKDataComponets.TIER_UPGRADE_DATA_COMPONENT_TYPE.get(), new TierUpgradeData(1));
        return new TierUpgradeItem(properties);
    }, MATERIALS);

    public static final RegistrySupplier<Item> ABYSS_BRACELET = item("abyss_bracelet", () -> {
        ProjectKItem.Properties properties = (ProjectKItem.Properties) new ProjectKItem.Properties().stacksTo(1);
        return new AbyssBraceletItem(properties);
    }, MATERIALS);

    public static final RegistrySupplier<Item> ABYSS_STAFF = item("abyss_staff", () -> {
        ProjectKItem.Properties properties = (ProjectKItem.Properties) new ProjectKItem.Properties().stacksTo(1);
        return new AbyssStaffItem(properties, 0, 30, 100L, 200L);
    }, MATERIALS);

    public static final RegistrySupplier<Item> ABYSS_ABSORPTION_PRISM_SHARD = item("abyss_absorption_prism_shard",
            () -> new AbyssAbsorptionPrismShard(new ProjectKItem.Properties().emc(128L)), MATERIALS);

    static {
    }

    @SuppressWarnings("unchecked")
    public static void init() {
        for (ProjectKBlocks.BlockItemInfo info : ProjectKBlocks.getBlockItemInfos()) {
            ItemsRegistry.blockItem((RegistrySupplier<Block>) info.block(), info.properties(), info.category());
        }
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
