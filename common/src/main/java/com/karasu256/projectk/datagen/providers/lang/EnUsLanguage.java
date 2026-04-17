package com.karasu256.projectk.datagen.providers.lang;

import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.energy.ProjectKEnergies;

import java.util.function.BiConsumer;

public class EnUsLanguage implements ProjectKLanguage {
    @Override
    public String getLocale() {
        return "en_us";
    }

    @Override
    public void addCommon(BiConsumer<String, String> adder) {
        adder.accept("category.projectk", "ProjectK");
        adder.accept("category.projectk.general", "ProjectK General");
        adder.accept("category.projectk.building_blocks", "ProjectK Building Blocks");
        adder.accept("category.projectk.ingot", "ProjectK Ingot");
        adder.accept("category.projectk.fluids", "ProjectK Fluids");
        adder.accept("category.projectk.machines", "ProjectK Machines");
        adder.accept("category.projectk.materials", "ProjectK Materials");
        adder.accept("gui.configscreen.title", "ProjectK Config");

        adder.accept("rei.category.projectk.in_biome_in_block_crafting", "In-Biome Block Crafting");
        adder.accept("rei.category.projectk.in_biome_in_block_crafting.requirement", "Biome required: %s");
        adder.accept("rei.category.projectk.in_biome_in_block_crafting.requirement_tag", "Biome tag required: %s");
        adder.accept("rei.category.projectk.abyss_magic_table", "Abyss Magic Table");
        adder.accept("rei.category.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        adder.accept("rei.category.projectk.abyss_synthesizer", "Abyss Synthesizer");

        adder.accept("energy.projectk.abyss_energy_format", "%s Abyss Energy");

        adder.accept("tooltip.projectk.abyss_wrench_behavior", "Mode: %s");
        adder.accept("tooltip.projectk.wthit.energy_none", "None");
        adder.accept("tooltip.projectk.wthit.energy_no_limit", "Energy: %s");
        adder.accept("tooltip.projectk.wthit.held_item", "Item: %s");
        adder.accept("tooltip.projectk.wthit.input_item", "Input: %s");
        adder.accept("tooltip.projectk.wthit.output_item", "Output: %s");
        adder.accept("tooltip.projectk.wthit.progress", "Progress: %s%%");
        adder.accept("tooltip.projectk.wthit.tier", "Tier: %s/%s");

        adder.accept("container.projectk.abyss_magic_table", "Abyss Magic Table");
        adder.accept("container.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        adder.accept("container.projectk.abyss_enchanter", "Abyss Enchanter");
        adder.accept("container.projectk.abyss_charger", "Abyss Charger");
        adder.accept("container.projectk.abyss_storage", "Abyss Storage");
        adder.accept("container.projectk.abyss_enchant_remover", "Abyss Enchant Remover");
        adder.accept("container.projectk.abyss_synthesizer", "Abyss Synthesizer");
        adder.accept("container.projectk.creative_abyss_storage", "Creative Abyss Storage");

        adder.accept("gui.projectk.dump", "Dump");
        adder.accept("tooltip.projectk.infinite", "Infinite");
        adder.accept("tooltip.projectk.energy_unit", "AE");
        adder.accept("tooltip.projectk.synthesizer.energy_total", "Total Energy: %s/%s");

        adder.accept("item.projectk.abyss_wrench", "Abyss Wrench");

        adder.accept("wrench_behavior.projectk.input", "Input");
        adder.accept("wrench_behavior.projectk.output", "Output");
        adder.accept("wrench_behavior.projectk.normal", "Normal");
        adder.accept("wrench_behavior.projectk.none", "None");
        adder.accept("wrench_behavior.projectk.downgrade", "Downgrade");

        adder.accept("block.projectk.abyss_generator", "Abyss Generator");
        adder.accept("block.projectk.abyss_magic_table", "Abyss Magic Table");
        adder.accept("block.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        adder.accept("block.projectk.abyss_enchanter", "Abyss Enchanter");
        adder.accept("block.projectk.abyss_charger", "Abyss Charger");
        adder.accept("block.projectk.abyss_storage", "Abyss Storage");
        adder.accept("block.projectk.creative_abyss_storage", "Creative Abyss Storage");
        adder.accept("block.projectk.abyss_energy_cable", "Abyss Energy Cable");
        adder.accept("block.projectk.karasium_ore", "Karasium Ore");
        adder.accept("block.projectk.deepslate_karasium_ore", "Deepslate Karasium Ore");
        adder.accept("block.projectk.abyss_enchant_remover", "Abyss Enchant Remover");
        adder.accept("block.projectk.abyss_synthesizer", "Abyss Synthesizer");
        adder.accept("block.projectk.abyss_laser_emitter", "Abyss Laser Emitter");
        adder.accept("block.projectk.abyss_absorption_prism", "Abyss Absorption Prism");
        adder.accept("block.projectk.abyss_portal", "Abyss Portal");
        adder.accept("block.projectk.polished_netherrack", "Polished Netherrack");

        adder.accept("block.projectk.abyss_core", "Abyss Core");

        adder.accept("item.projectk.wither_bone", "Wither Bone");
        adder.accept("item.projectk.karasium", "Karasium");
        adder.accept("item.projectk.raw_karasium", "Raw Karasium");
        adder.accept("item.projectk.karasium_dust", "Karasium Dust");
        adder.accept("item.projectk.abyss_ingot", "Abyss Ingot");
        adder.accept("item.projectk.tier_upgrade", "Tier Upgrade");
        adder.accept("item.projectk.abyss_bracelet", "Abyss Bracelet");
        adder.accept("item.projectk.abyss_staff", "Abyss Staff");
        adder.accept("item.projectk.abyss_absorption_prism_shard", "Abyss Absorption Prism Shard");

        adder.accept("enchantment.projectk.abyss_booster", "Abyss Booster");
    }

    @Override
    public void addEnergy(ProjectKEnergies.EnergyDefinition definition, BiConsumer<String, String> adder) {
        adder.accept("energy.projectk." + definition.idPath(), definition.enName());
        String typeKey = "tooltip.projectk.energy_type." + (definition.kind() == ProjectKEnergies.EnergyKind.NEUTRAL ? "abyss" : definition.kind()
                .name().toLowerCase());
        adder.accept(typeKey, definition.enName().replaceAll("§.", ""));
    }

    @Override
    public void addEnergyItem(ProjectKEnergies.EnergyDefinition definition, String energyName, BiConsumer<String, String> adder) {
        String bucketKey = "item.projectk.bucket_of_" + definition.idPath();
        if (definition.isBase()) {
            adder.accept(bucketKey, "Bucket of Abyss Energy");
        } else {
            adder.accept(bucketKey, "Bucket of " + energyName + " Abyss Energy");
        }
    }

    @Override
    public void addEnergyBlock(ProjectKEnergies.EnergyDefinition definition, String energyName, BiConsumer<String, String> adder) {
        String coreKey = "block.projectk." + definition.idPath() + "_core";
        if (definition.isBase()) {
            adder.accept(coreKey, "Abyss Core");
        } else {
            adder.accept(coreKey, energyName + " Abyss Core");
        }
    }

    @Override
    public void addBlockSet(String baseName, BlockMaterials material, String key, BiConsumer<String, String> adder) {
        if (material == BlockMaterials.STAIR) {
            adder.accept(key, baseName + " Stairs");
        } else if (material == BlockMaterials.HALF) {
            adder.accept(key, baseName + " Slab");
        }
    }
}
