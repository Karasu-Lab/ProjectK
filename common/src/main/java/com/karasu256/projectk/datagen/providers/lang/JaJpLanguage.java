package com.karasu256.projectk.datagen.providers.lang;

import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.energy.ProjectKEnergies;

import java.util.function.BiConsumer;

public class JaJpLanguage implements ProjectKLanguage {
    @Override
    public String getLocale() {
        return "ja_jp";
    }

    @Override
    public void addCommon(BiConsumer<String, String> adder) {
        adder.accept("category.projectk.general", "ProjectK 全般");
        adder.accept("category.projectk.building_blocks", "ProjectK 建築ブロック");
        adder.accept("category.projectk.ingot", "ProjectK インゴット");
        adder.accept("category.projectk.fluids", "ProjectK 液体");
        adder.accept("category.projectk.machines", "ProjectK 機械");
        adder.accept("category.projectk.materials", "ProjectK 材料");

        adder.accept("rei.category.projectk.in_biome_in_block_crafting", "バイオーム内ブロッククラフト");
        adder.accept("rei.category.projectk.in_biome_in_block_crafting.requirement", "必要バイオーム: %s");
        adder.accept("rei.category.projectk.in_biome_in_block_crafting.requirement_tag", "必要バイオームタグ: %s");
        adder.accept("rei.category.projectk.abyss_magic_table", "深淵魔術テーブル");
        adder.accept("rei.category.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        adder.accept("rei.category.projectk.abyss_synthesizer", "深淵合成機");

        adder.accept("energy.projectk.abyss_energy_format", "%sの深淵エネルギー");

        adder.accept("tooltip.projectk.abyss_wrench_behavior", "モード: %s");
        adder.accept("tooltip.projectk.wthit.energy_type", "エネルギータイプ");
        adder.accept("tooltip.projectk.wthit.energy_none", "なし");
        adder.accept("tooltip.projectk.wthit.energy_no_limit", "エネルギー: %s");
        adder.accept("tooltip.projectk.wthit.held_item", "アイテム: %s");
        adder.accept("tooltip.projectk.wthit.input_item", "入力: %s");
        adder.accept("tooltip.projectk.wthit.output_item", "出力: %s");
        adder.accept("tooltip.projectk.wthit.progress", "進捗: %s%%");
        adder.accept("tooltip.projectk.wthit.tier", "ティア: %s/%s");

        adder.accept("container.projectk.abyss_magic_table", "深淵魔術テーブル");
        adder.accept("container.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        adder.accept("container.projectk.abyss_enchanter", "深淵エンチャンター");
        adder.accept("container.projectk.abyss_charger", "深淵チャージャー");
        adder.accept("container.projectk.abyss_storage", "深淵貯蔵機");
        adder.accept("container.projectk.abyss_enchant_remover", "深淵エンチャントリムーバー");
        adder.accept("container.projectk.abyss_synthesizer", "深淵合成機");
        adder.accept("container.projectk.creative_abyss_storage", "クリエイティブ深淵貯蔵機");

        adder.accept("gui.projectk.dump", "破棄");
        adder.accept("tooltip.projectk.infinite", "無限");
        adder.accept("tooltip.projectk.energy_unit", "AE");
        adder.accept("tooltip.projectk.synthesizer.energy_total", "合計エネルギー: %s/%s");

        adder.accept("item.projectk.abyss_wrench", "深淵レンチ");

        adder.accept("wrench_behavior.projectk.input", "入力");
        adder.accept("wrench_behavior.projectk.output", "出力");
        adder.accept("wrench_behavior.projectk.normal", "通常");
        adder.accept("wrench_behavior.projectk.none", "なし");
        adder.accept("wrench_behavior.projectk.downgrade", "ダウングレード");

        adder.accept("block.projectk.abyss_generator", "深淵発電機");
        adder.accept("block.projectk.abyss_magic_table", "深淵魔術テーブル");
        adder.accept("block.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        adder.accept("block.projectk.abyss_enchanter", "深淵エンチャンター");
        adder.accept("block.projectk.abyss_charger", "深淵チャージャー");
        adder.accept("block.projectk.abyss_storage", "深淵貯蔵機");
        adder.accept("block.projectk.creative_abyss_storage", "クリエイティブ深淵貯蔵機");
        adder.accept("block.projectk.abyss_energy_cable", "深淵エネルギーケーブル");
        adder.accept("block.projectk.abyss_synthesizer", "深淵合成機");
        adder.accept("block.projectk.abyss_laser_emitter", "深淵レーザー発射機");
        adder.accept("block.projectk.abyss_absorption_prism", "深淵吸収プリズム");
        adder.accept("block.projectk.abyss_portal", "深淵ポータル");
        adder.accept("block.projectk.polished_netherrack", "磨かれたネザーラック");
        adder.accept("block.projectk.karasium_ore", "カラシウム鉱石");
        adder.accept("block.projectk.deepslate_karasium_ore", "深層カラシウム鉱石");
        adder.accept("block.projectk.abyss_enchant_remover", "深淵エンチャントリムーバー");

        adder.accept("block.projectk.abyss_core", "深淵コア");

        adder.accept("item.projectk.wither_bone", "ウィザーの骨");
        adder.accept("item.projectk.karasium", "カラシウム");
        adder.accept("item.projectk.raw_karasium", "カラシウム原石");
        adder.accept("item.projectk.karasium_dust", "カラシウムダスト");
        adder.accept("item.projectk.abyss_ingot", "深淵インゴット");
        adder.accept("item.projectk.tier_upgrade", "ティアアップグレード");
        adder.accept("item.projectk.abyss_bracelet", "深淵ブレスレット");
        adder.accept("item.projectk.abyss_staff", "深淵の杖");
        adder.accept("item.projectk.abyss_absorption_prism_shard", "深淵吸収プリズムの欠片");

        adder.accept("enchantment.projectk.abyss_booster", "深淵ブースター");
    }

    @Override
    public void addEnergy(ProjectKEnergies.EnergyDefinition definition, BiConsumer<String, String> adder) {
        adder.accept("energy.projectk." + definition.idPath(), definition.jaName());
        String typeKey = "tooltip.projectk.energy_type." +
                (definition.kind() == ProjectKEnergies.EnergyKind.NEUTRAL ? "abyss" : definition.kind().name()
                        .toLowerCase());
        adder.accept(typeKey, definition.jaName().replaceAll("§.", ""));
    }

    @Override
    public void addEnergyItem(ProjectKEnergies.EnergyDefinition definition, String energyName, BiConsumer<String, String> adder) {
        String bucketKey = "item.projectk.bucket_of_" + definition.idPath();
        if (definition.isBase()) {
            adder.accept(bucketKey, "深淵入りバケツ");
        } else {
            adder.accept(bucketKey, energyName + "の深淵入りバケツ");
        }
    }

    @Override
    public void addEnergyBlock(ProjectKEnergies.EnergyDefinition definition, String energyName, BiConsumer<String, String> adder) {
        String coreKey = "block.projectk." + definition.idPath() + "_core";
        if (definition.isBase()) {
            adder.accept(coreKey, "深淵コア");
        } else {
            adder.accept(coreKey, energyName + "の深淵コア");
        }
    }

    @Override
    public void addBlockSet(String baseName, BlockMaterials material, String key, BiConsumer<String, String> adder) {
        if (material == BlockMaterials.STAIR) {
            adder.accept(key, baseName + "の階段");
        } else if (material == BlockMaterials.HALF) {
            adder.accept(key, baseName + "のハーフブロック");
        }
    }
}
