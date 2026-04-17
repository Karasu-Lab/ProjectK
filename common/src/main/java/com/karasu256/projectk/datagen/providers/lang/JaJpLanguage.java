package com.karasu256.projectk.datagen.providers.lang;

import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.CreativeTabsRegistry;

public class JaJpLanguage implements ProjectKLanguage {
    @Override
    public String getLocale() {
        return "ja_jp";
    }

    @Override
    public void addCommon(TranslationAdder adder) {
        adder.add("category.projectk", "ProjectK");
        adder.add(CreativeTabsRegistry.GENERAL, "ProjectK 全般");
        adder.add(CreativeTabsRegistry.BUILDING_BLOCKS, "ProjectK 建築ブロック");
        adder.add(CreativeTabsRegistry.INGOT, "ProjectK インゴット");
        adder.add(CreativeTabsRegistry.FLUIDS, "ProjectK 液体");
        adder.add(CreativeTabsRegistry.MACHINES, "ProjectK 機械");
        adder.add(CreativeTabsRegistry.MATERIALS, "ProjectK 材料");

        adder.add("rei.category.projectk.in_biome_in_block_crafting", "バイオーム内ブロッククラフト");
        adder.add("rei.category.projectk.in_biome_in_block_crafting.requirement", "必要バイオーム: %s");
        adder.add("rei.category.projectk.in_biome_in_block_crafting.requirement_tag", "必要バイオームタグ: %s");
        adder.add("rei.category.projectk.abyss_magic_table", "深淵魔術テーブル");
        adder.add("rei.category.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        adder.add("rei.category.projectk.abyss_synthesizer", "深淵合成機");

        adder.add("energy.projectk.abyss_energy_format", "%sの深淵エネルギー");

        adder.add("tooltip.projectk.abyss_wrench_behavior", "モード: %s");
        adder.add("tooltip.projectk.wthit.energy_type", "エネルギータイプ");
        adder.add("tooltip.projectk.wthit.energy_none", "なし");
        adder.add("tooltip.projectk.wthit.energy_no_limit", "エネルギー: %s");
        adder.add("tooltip.projectk.wthit.held_item", "アイテム: %s");
        adder.add("tooltip.projectk.wthit.input_item", "入力: %s");
        adder.add("tooltip.projectk.wthit.output_item", "出力: %s");
        adder.add("tooltip.projectk.wthit.progress", "進捗: %s%%");
        adder.add("tooltip.projectk.wthit.tier", "ティア: %s/%s");

        adder.add("container.projectk.abyss_magic_table", "深淵魔術テーブル");
        adder.add("container.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        adder.add("container.projectk.abyss_enchanter", "深淵エンチャンター");
        adder.add("container.projectk.abyss_charger", "深淵チャージャー");
        adder.add("container.projectk.abyss_storage", "深淵貯蔵機");
        adder.add("container.projectk.abyss_enchant_remover", "深淵エンチャントリムーバー");
        adder.add("container.projectk.abyss_synthesizer", "深淵合成機");
        adder.add("container.projectk.creative_abyss_storage", "クリエイティブ深淵貯蔵機");

        adder.add("gui.projectk.dump", "破棄");
        adder.add("tooltip.projectk.infinite", "無限");
        adder.add("tooltip.projectk.energy_unit", "AE");
        adder.add("tooltip.projectk.synthesizer.energy_total", "合計エネルギー: %s/%s");

        adder.add("item.projectk.abyss_wrench", "深淵レンチ");

        adder.add("wrench_behavior.projectk.input", "入力");
        adder.add("wrench_behavior.projectk.output", "出力");
        adder.add("wrench_behavior.projectk.normal", "通常");
        adder.add("wrench_behavior.projectk.none", "なし");
        adder.add("wrench_behavior.projectk.downgrade", "ダウングレード");

        adder.add(ProjectKBlocks.ABYSS_GENERATOR.get(), "深淵発電機");
        adder.add(ProjectKBlocks.ABYSS_MAGIC_TABLE.get(), "深淵魔術テーブル");
        adder.add(ProjectKBlocks.ABYSS_ALCHEMY_BLEND_MACHINE.get(), "深淵錬金合成機");
        adder.add(ProjectKBlocks.ABYSS_ENCHANTER.get(), "深淵エンチャンター");
        adder.add(ProjectKBlocks.ABYSS_CHARGER.get(), "深淵チャージャー");
        adder.add(ProjectKBlocks.ABYSS_STORAGE.get(), "深淵貯蔵機");
        adder.add(ProjectKBlocks.CREATIVE_ABYSS_STORAGE.get(), "クリエイティブ深淵貯蔵機");
        adder.add(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), "深淵エネルギーケーブル");
        adder.add(ProjectKBlocks.ABYSS_SYNTHESIZER.get(), "深淵合成機");
        adder.add(ProjectKBlocks.ABYSS_LASER_EMITTER.get(), "深淵レーザー発射機");
        adder.add(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get(), "深淵吸収プリズム");
        adder.add(ProjectKBlocks.ABYSS_PORTAL.get(), "深淵ポータル");
        adder.add(ProjectKBlocks.POLISHED_NETHERRACK.get(), "磨かれたネザーラック");
        adder.add(ProjectKBlocks.POLISHED_GLOWSTONE.get(), "磨かれたグロウストーン");
        adder.add(ProjectKBlocks.KARASIUM_ORE.get(), "カラシウム鉱石");
        adder.add(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.get(), "深層カラシウム鉱石");
        adder.add(ProjectKBlocks.ABYSS_ENCHANT_REMOVER.get(), "深淵エンチャントリムーバー");

        adder.add(ProjectKBlocks.ABYSS_CORE.get(), "深淵コア");

        adder.add(ProjectKItems.WITHER_BONE.get(), "ウィザーの骨");
        adder.add(ProjectKItems.KARASIUM.get(), "カラシウム");
        adder.add(ProjectKItems.RAW_KARASIUM.get(), "カラシウム原石");
        adder.add(ProjectKItems.KARASIUM_DUST.get(), "カラシウムダスト");
        adder.add(ProjectKItems.ABYSS_INGOT.get(), "深淵インゴット");
        adder.add(ProjectKItems.TIER_UPGRADE.get(), "ティアアップグレード");
        adder.add(ProjectKItems.ABYSS_BRACELET.get(), "深淵ブレスレット");
        adder.add(ProjectKItems.ABYSS_STAFF.get(), "深淵の杖");
        adder.add(ProjectKItems.ABYSS_ABSORPTION_PRISM_SHARD.get(), "深淵吸収プリズムの欠片");

        adder.add("enchantment.projectk.abyss_booster", "深淵ブースター");
    }

    @Override
    public void addEnergy(ProjectKEnergies.EnergyDefinition definition, TranslationAdder adder) {
        adder.add("energy.projectk." + definition.idPath(), definition.jaName());
        String typeKey = "tooltip.projectk.energy_type." + (definition.kind() == ProjectKEnergies.EnergyKind.NEUTRAL ? "abyss" : definition.kind()
                .name().toLowerCase());
        adder.add(typeKey, definition.jaName().replaceAll("§.", ""));
    }

    @Override
    public void addEnergyItem(ProjectKEnergies.EnergyDefinition definition, String energyName, TranslationAdder adder) {
        String bucketKey = "item.projectk.bucket_of_" + definition.idPath();
        if (definition.isBase()) {
            adder.add(bucketKey, "深淵入りバケツ");
        } else {
            adder.add(bucketKey, energyName + "の深淵入りバケツ");
        }
    }

    @Override
    public void addEnergyBlock(ProjectKEnergies.EnergyDefinition definition, String energyName, TranslationAdder adder) {
        String coreKey = "block.projectk." + definition.idPath() + "_core";
        if (definition.isBase()) {
            adder.add(coreKey, "深淵コア");
        } else {
            adder.add(coreKey, energyName + "の深淵コア");
        }
    }

    @Override
    public void addBlockSet(String baseName, BlockMaterials material, String key, TranslationAdder adder) {
        if (material == BlockMaterials.STAIR) {
            adder.add(key, baseName + "の階段");
        } else if (material == BlockMaterials.HALF) {
            adder.add(key, baseName + "のハーフブロック");
        }
    }
}
