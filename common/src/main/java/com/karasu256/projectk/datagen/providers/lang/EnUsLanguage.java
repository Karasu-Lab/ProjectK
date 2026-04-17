package com.karasu256.projectk.datagen.providers.lang;

import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.CreativeTabsRegistry;

public class EnUsLanguage implements ProjectKLanguage {
    @Override
    public String getLocale() {
        return "en_us";
    }

    @Override
    public void addCommon(TranslationAdder adder) {
        adder.add("category.projectk", "ProjectK");
        adder.add(CreativeTabsRegistry.GENERAL, "ProjectK General");
        adder.add(CreativeTabsRegistry.BUILDING_BLOCKS, "ProjectK Building Blocks");
        adder.add(CreativeTabsRegistry.INGOT, "ProjectK Ingot");
        adder.add(CreativeTabsRegistry.FLUIDS, "ProjectK Fluids");
        adder.add(CreativeTabsRegistry.MACHINES, "ProjectK Machines");
        adder.add(CreativeTabsRegistry.MATERIALS, "ProjectK Materials");
        adder.add("gui.configscreen.title", "ProjectK Config");

        adder.add("rei.category.projectk.in_biome_in_block_crafting", "In-Biome Block Crafting");
        adder.add("rei.category.projectk.in_biome_in_block_crafting.requirement", "Biome required: %s");
        adder.add("rei.category.projectk.in_biome_in_block_crafting.requirement_tag", "Biome tag required: %s");
        adder.add("rei.category.projectk.abyss_magic_table", "Abyss Magic Table");
        adder.add("rei.category.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        adder.add("rei.category.projectk.abyss_synthesizer", "Abyss Synthesizer");

        adder.add("energy.projectk.abyss_energy_format", "%s Abyss Energy");

        adder.add("tooltip.projectk.abyss_wrench_behavior", "Mode: %s");
        adder.add("tooltip.projectk.wthit.energy_none", "None");
        adder.add("tooltip.projectk.wthit.energy_no_limit", "Energy: %s");
        adder.add("tooltip.projectk.wthit.held_item", "Item: %s");
        adder.add("tooltip.projectk.wthit.input_item", "Input: %s");
        adder.add("tooltip.projectk.wthit.output_item", "Output: %s");
        adder.add("tooltip.projectk.wthit.progress", "Progress: %s%%");
        adder.add("tooltip.projectk.wthit.tier", "Tier: %s/%s");

        adder.add("container.projectk.abyss_magic_table", "Abyss Magic Table");
        adder.add("container.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        adder.add("container.projectk.abyss_enchanter", "Abyss Enchanter");
        adder.add("container.projectk.abyss_charger", "Abyss Charger");
        adder.add("container.projectk.abyss_storage", "Abyss Storage");
        adder.add("container.projectk.abyss_enchant_remover", "Abyss Enchant Remover");
        adder.add("container.projectk.abyss_synthesizer", "Abyss Synthesizer");
        adder.add("container.projectk.creative_abyss_storage", "Creative Abyss Storage");

        adder.add("gui.projectk.dump", "Dump");
        adder.add("tooltip.projectk.infinite", "Infinite");
        adder.add("tooltip.projectk.energy_unit", "AE");
        adder.add("tooltip.projectk.synthesizer.energy_total", "Total Energy: %s/%s");

        adder.add(ProjectKItems.ABYSS_WRENCH.get(), "Abyss Wrench");

        adder.add("wrench_behavior.projectk.input", "Input");
        adder.add("wrench_behavior.projectk.output", "Output");
        adder.add("wrench_behavior.projectk.normal", "Normal");
        adder.add("wrench_behavior.projectk.none", "None");
        adder.add("wrench_behavior.projectk.downgrade", "Downgrade");

        adder.add(ProjectKBlocks.ABYSS_GENERATOR.get(), "Abyss Generator");
        adder.add(ProjectKBlocks.ABYSS_MAGIC_TABLE.get(), "Abyss Magic Table");
        adder.add(ProjectKBlocks.ABYSS_ALCHEMY_BLEND_MACHINE.get(), "Abyss Alchemy Blend Machine");
        adder.add(ProjectKBlocks.ABYSS_ENCHANTER.get(), "Abyss Enchanter");
        adder.add(ProjectKBlocks.ABYSS_CHARGER.get(), "Abyss Charger");
        adder.add(ProjectKBlocks.ABYSS_STORAGE.get(), "Abyss Storage");
        adder.add(ProjectKBlocks.CREATIVE_ABYSS_STORAGE.get(), "Creative Abyss Storage");
        adder.add(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), "Abyss Energy Cable");
        adder.add(ProjectKBlocks.KARASIUM_ORE.get(), "Karasium Ore");
        adder.add(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.get(), "Deepslate Karasium Ore");
        adder.add(ProjectKBlocks.ABYSS_ENCHANT_REMOVER.get(), "Abyss Enchant Remover");
        adder.add(ProjectKBlocks.ABYSS_SYNTHESIZER.get(), "Abyss Synthesizer");
        adder.add(ProjectKBlocks.ABYSS_LASER_EMITTER.get(), "Abyss Laser Emitter");
        adder.add(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get(), "Abyss Absorption Prism");
        adder.add(ProjectKBlocks.ABYSS_PORTAL.get(), "Abyss Portal");
        adder.add(ProjectKBlocks.POLISHED_NETHERRACK.get(), "Polished Netherrack");
        adder.add(ProjectKBlocks.POLISHED_GLOWSTONE.get(), "Polished Growstone");

        adder.add(ProjectKBlocks.ABYSS_CORE.get(), "Abyss Core");

        adder.add(ProjectKItems.WITHER_BONE.get(), "Wither Bone");
        adder.add(ProjectKItems.KARASIUM.get(), "Karasium");
        adder.add(ProjectKItems.RAW_KARASIUM.get(), "Raw Karasium");
        adder.add(ProjectKItems.KARASIUM_DUST.get(), "Karasium Dust");
        adder.add(ProjectKItems.ABYSS_INGOT.get(), "Abyss Ingot");
        adder.add(ProjectKItems.TIER_UPGRADE.get(), "Tier Upgrade");
        adder.add(ProjectKItems.ABYSS_BRACELET.get(), "Abyss Bracelet");
        adder.add(ProjectKItems.ABYSS_STAFF.get(), "Abyss Staff");
        adder.add(ProjectKItems.ABYSS_ABSORPTION_PRISM_SHARD.get(), "Abyss Absorption Prism Shard");

        adder.add("enchantment.projectk.abyss_booster", "Abyss Booster");
    }

    @Override
    public void addEnergy(ProjectKEnergies.EnergyDefinition definition, TranslationAdder adder) {
        adder.add("energy.projectk." + definition.idPath(), definition.enName());
        String typeKey = "tooltip.projectk.energy_type." + (definition.kind() == ProjectKEnergies.EnergyKind.NEUTRAL ? "abyss" : definition.kind()
                .name().toLowerCase());
        adder.add(typeKey, definition.enName().replaceAll("§.", ""));
    }

    @Override
    public void addEnergyItem(ProjectKEnergies.EnergyDefinition definition, String energyName, TranslationAdder adder) {
        String bucketKey = "item.projectk.bucket_of_" + definition.idPath();
        if (definition.isBase()) {
            adder.add(bucketKey, "Bucket of Abyss Energy");
        } else {
            adder.add(bucketKey, "Bucket of " + energyName + " Abyss Energy");
        }
    }

    @Override
    public void addEnergyBlock(ProjectKEnergies.EnergyDefinition definition, String energyName, TranslationAdder adder) {
        String coreKey = "block.projectk." + definition.idPath() + "_core";
        if (definition.isBase()) {
            adder.add(coreKey, "Abyss Core");
        } else {
            adder.add(coreKey, energyName + " Abyss Core");
        }
    }

    @Override
    public void addBlockSet(String baseName, BlockMaterials material, String key, TranslationAdder adder) {
        if (material == BlockMaterials.STAIR) {
            adder.add(key, baseName + " Stairs");
        } else if (material == BlockMaterials.HALF) {
            adder.add(key, baseName + " Slab");
        }
    }
}
