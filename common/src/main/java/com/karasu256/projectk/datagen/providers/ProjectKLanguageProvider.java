package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.energy.PKMaterials;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ProjectKLanguageProvider implements DataProvider {
    private final PathProvider langPathProvider;

    public ProjectKLanguageProvider(PackOutput output) {
        this.langPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        JsonObject en = new JsonObject();
        JsonObject ja = new JsonObject();

        addCommonTranslations(en, ja);
        addEnergyTranslations(en, ja);
        addEnergyItemTranslations(en, ja);
        addEnergyBlockTranslations(en, ja);

        CompletableFuture<?> enWrite = writeLang(output, "en_us", en);
        CompletableFuture<?> jaWrite = writeLang(output, "ja_jp", ja);
        return CompletableFuture.allOf(enWrite, jaWrite);
    }

    @Override
    public String getName() {
        return "ProjectK Languages";
    }

    private void addCommonTranslations(JsonObject en, JsonObject ja) {
        add(en, "category.projectk", "ProjectK");
        add(en, "gui.configscreen.title", "ProjectK Config");

        add(en, "rei.category.projectk.in_biome_in_block_crafting", "In-Biome Block Crafting");
        add(en, "rei.category.projectk.in_biome_in_block_crafting.requirement", "Biome required: %s");
        add(en, "rei.category.projectk.in_biome_in_block_crafting.requirement_tag", "Biome tag required: %s");
        add(en, "rei.category.projectk.abyss_magic_table", "Abyss Magic Table");
        add(en, "rei.category.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        add(en, "rei.category.projectk.abyss_synthesizer", "Abyss Synthesizer");

        add(ja, "rei.category.projectk.in_biome_in_block_crafting", "バイオーム内ブロッククラフト");
        add(ja, "rei.category.projectk.in_biome_in_block_crafting.requirement", "必要バイオーム: %s");
        add(ja, "rei.category.projectk.in_biome_in_block_crafting.requirement_tag", "必要バイオームタグ: %s");
        add(ja, "rei.category.projectk.abyss_magic_table", "深淵魔術テーブル");
        add(ja, "rei.category.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        add(ja, "rei.category.projectk.abyss_synthesizer", "深淵合成機");

        add(en, "energy.projectk.abyss_energy_format", "%s Abyss Energy");
        add(ja, "energy.projectk.abyss_energy_format", "%sの深淵エネルギー");

        add(en, "tooltip.projectk.abyss_wrench_behavior", "Mode: %s");
        add(en, "tooltip.projectk.wthit.energy_none", "None");
        add(en, "tooltip.projectk.wthit.energy_no_limit", "Energy: %s");
        add(en, "tooltip.projectk.wthit.held_item", "Item: %s");
        add(en, "tooltip.projectk.wthit.input_item", "Input: %s");
        add(en, "tooltip.projectk.wthit.output_item", "Output: %s");
        add(en, "tooltip.projectk.wthit.progress", "Progress: %s%%");
        add(en, "tooltip.projectk.wthit.tier", "Tier: %s/%s");

        add(ja, "tooltip.projectk.abyss_wrench_behavior", "モード: %s");
        add(ja, "tooltip.projectk.wthit.energy_type", "エネルギータイプ");
        add(ja, "tooltip.projectk.wthit.energy_none", "なし");
        add(ja, "tooltip.projectk.wthit.energy_no_limit", "エネルギー: %s");
        add(ja, "tooltip.projectk.wthit.held_item", "アイテム: %s");
        add(ja, "tooltip.projectk.wthit.input_item", "入力: %s");
        add(ja, "tooltip.projectk.wthit.output_item", "出力: %s");
        add(ja, "tooltip.projectk.wthit.progress", "進捗: %s%%");
        add(ja, "tooltip.projectk.wthit.tier", "ティア: %s/%s");

        add(en, "container.projectk.abyss_magic_table", "Abyss Magic Table");
        add(en, "container.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        add(en, "container.projectk.abyss_enchanter", "Abyss Enchanter");
        add(en, "container.projectk.abyss_charger", "Abyss Charger");
        add(en, "container.projectk.abyss_storage", "Abyss Storage");
        add(ja, "container.projectk.abyss_magic_table", "深淵魔術テーブル");
        add(ja, "container.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        add(ja, "container.projectk.abyss_enchanter", "深淵エンチャンター");
        add(ja, "container.projectk.abyss_charger", "深淵チャージャー");
        add(ja, "container.projectk.abyss_storage", "深淵貯蔵機");
        add(en, "container.projectk.abyss_enchant_remover", "Abyss Enchant Remover");
        add(en, "container.projectk.abyss_synthesizer", "Abyss Synthesizer");
        add(ja, "container.projectk.abyss_enchant_remover", "深淵エンチャントリムーバー");
        add(ja, "container.projectk.abyss_synthesizer", "深淵合成機");

        add(en, "container.projectk.creative_abyss_storage", "Creative Abyss Storage");
        add(ja, "container.projectk.creative_abyss_storage", "クリエイティブ深淵貯蔵機");

        add(en, "gui.projectk.dump", "Dump");
        add(ja, "gui.projectk.dump", "破棄");

        add(en, "tooltip.projectk.infinite", "Infinite");
        add(ja, "tooltip.projectk.infinite", "無限");

        add(en, "tooltip.projectk.energy_unit", "AE");
        add(ja, "tooltip.projectk.energy_unit", "AE");

        add(en, "tooltip.projectk.synthesizer.energy_total", "Total Energy: %s/%s");
        add(ja, "tooltip.projectk.synthesizer.energy_total", "合計エネルギー: %s/%s");

        add(en, "item.projectk.abyss_wrench", "Abyss Wrench");
        add(ja, "item.projectk.abyss_wrench", "深淵レンチ");

        add(en, "wrench_behavior.projectk.input", "Input");
        add(en, "wrench_behavior.projectk.output", "Output");
        add(en, "wrench_behavior.projectk.normal", "Normal");
        add(en, "wrench_behavior.projectk.none", "None");
        add(en, "wrench_behavior.projectk.downgrade", "Downgrade");

        add(ja, "wrench_behavior.projectk.input", "入力");
        add(ja, "wrench_behavior.projectk.output", "出力");
        add(ja, "wrench_behavior.projectk.normal", "通常");
        add(ja, "wrench_behavior.projectk.none", "なし");
        add(ja, "wrench_behavior.projectk.downgrade", "ダウングレード");

        add(en, "block.projectk.abyss_generator", "Abyss Generator");
        add(en, "block.projectk.abyss_magic_table", "Abyss Magic Table");
        add(en, "block.projectk.abyss_alchemy_blend_machine", "Abyss Alchemy Blend Machine");
        add(en, "block.projectk.abyss_enchanter", "Abyss Enchanter");
        add(en, "block.projectk.abyss_charger", "Abyss Charger");
        add(en, "block.projectk.abyss_storage", "Abyss Storage");
        add(en, "block.projectk.creative_abyss_storage", "Creative Abyss Storage");
        add(en, "block.projectk.abyss_energy_cable", "Abyss Energy Cable");
        add(en, "block.projectk.karasium_ore", "Karasium Ore");
        add(en, "block.projectk.deepslate_karasium_ore", "Deepslate Karasium Ore");
        add(en, "block.projectk.abyss_enchant_remover", "Abyss Enchant Remover");
        add(en, "block.projectk.abyss_synthesizer", "Abyss Synthesizer");
        add(en, "block.projectk.abyss_laser_emitter", "Abyss Laser Emitter");
        add(en, "block.projectk.abyss_absorption_prism", "Abyss Absorption Prism");
        add(en, "block.projectk.abyss_portal", "Abyss Portal");
        add(ja, "block.projectk.abyss_enchant_remover", "深淵エンチャントリムーバー");

        add(ja, "block.projectk.abyss_generator", "深淵発電機");
        add(ja, "block.projectk.abyss_magic_table", "深淵魔術テーブル");
        add(ja, "block.projectk.abyss_alchemy_blend_machine", "深淵錬金合成機");
        add(ja, "block.projectk.abyss_enchanter", "深淵エンチャンター");
        add(ja, "block.projectk.abyss_charger", "深淵チャージャー");
        add(ja, "block.projectk.abyss_storage", "深淵貯蔵機");
        add(ja, "block.projectk.creative_abyss_storage", "クリエイティブ深淵貯蔵機");
        add(ja, "block.projectk.abyss_energy_cable", "深淵エネルギーケーブル");
        add(ja, "block.projectk.abyss_synthesizer", "深淵合成機");
        add(ja, "block.projectk.abyss_laser_emitter", "深淵レーザー発射機");
        add(ja, "block.projectk.abyss_absorption_prism", "深淵吸収プリズム");
        add(ja, "block.projectk.abyss_portal", "深淵ポータル");
        add(ja, "block.projectk.karasium_ore", "カラシウム鉱石");
        add(ja, "block.projectk.deepslate_karasium_ore", "深層カラシウム鉱石");

        add(en, "item.projectk.wither_bone", "Wither Bone");
        add(en, "item.projectk.karasium", "Karasium");
        add(en, "item.projectk.raw_karasium", "Raw Karasium");
        add(en, "item.projectk.karasium_dust", "Karasium Dust");
        add(en, "item.projectk.abyss_ingot", "Abyss Ingot");
        add(en, "item.projectk.tier_upgrade", "Tier Upgrade");
        add(en, "item.projectk.abyss_bracelet", "Abyss Bracelet");
        add(en, "item.projectk.abyss_absorption_prism_shard", "Abyss Absorption Prism Shard");

        add(ja, "item.projectk.wither_bone", "ウィザーの骨");
        add(ja, "item.projectk.karasium", "カラシウム");
        add(ja, "item.projectk.raw_karasium", "カラシウム原石");
        add(ja, "item.projectk.karasium_dust", "カラシウムダスト");
        add(ja, "item.projectk.abyss_ingot", "深淵インゴット");
        add(ja, "item.projectk.tier_upgrade", "ティアアップグレード");
        add(ja, "item.projectk.abyss_bracelet", "深淵ブレスレット");
        add(ja, "item.projectk.abyss_absorption_prism_shard", "深淵吸収プリズムの欠片");

        add(en, "enchantment.projectk.abyss_booster", "Abyss Booster");
        add(ja, "enchantment.projectk.abyss_booster", "深淵ブースター");
    }

    private void addEnergyTranslations(JsonObject en, JsonObject ja) {
        for (PKMaterials material : PKMaterials.values()) {
            String key = energyKey(material);
            add(en, key, material.enName());
            add(ja, key, material.jaName());

            String typeKey = "tooltip.projectk.energy_type." +
                    (material.kind() == ProjectKEnergies.EnergyKind.NEUTRAL ? "abyss" : material.kind().name()
                            .toLowerCase());

            String enName = material.enName().replaceAll("§.", "");
            String jaName = material.jaName().replaceAll("§.", "");

            add(en, typeKey, enName);
            add(ja, typeKey, jaName);
        }
    }

    private void addEnergyItemTranslations(JsonObject en, JsonObject ja) {
        for (PKMaterials material : PKMaterials.values()) {
            String energyLabelEn = energyNameWithReset(en, material);
            String energyLabelJa = energyNameWithReset(ja, material);

            String bucketKey = "item.projectk.bucket_of_" + material.energyIdPath();
            if (material.kind() == ProjectKEnergies.EnergyKind.NEUTRAL) {
                add(en, bucketKey, "Bucket of Abyss Energy");
                add(ja, bucketKey, "深淵入りバケツ");
            } else {
                add(en, bucketKey, "Bucket of " + energyLabelEn + " Abyss Energy");
                add(ja, bucketKey, energyLabelJa + "の深淵入りバケツ");
            }
        }
    }

    private void addEnergyBlockTranslations(JsonObject en, JsonObject ja) {
        for (PKMaterials material : PKMaterials.values()) {
            String energyLabelEn = energyNameWithReset(en, material);
            String energyLabelJa = energyNameWithReset(ja, material);
            String coreKey = "block.projectk." + material.id() + "_core";
            if (material.kind() == ProjectKEnergies.EnergyKind.NEUTRAL) {
                add(en, coreKey, "Abyss Core");
                add(ja, coreKey, "深淵コア");
            } else {
                add(en, coreKey, energyLabelEn + " Abyss Core");
                add(ja, coreKey, energyLabelJa + "の深淵コア");
            }
        }
    }

    private CompletableFuture<?> writeLang(CachedOutput output, String locale, JsonObject json) {
        Path file = langPathProvider.json(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, locale));
        return DataProvider.saveStable(output, json, file);
    }

    private void add(JsonObject json, String key, String value) {
        json.addProperty(key, value);
    }

    private String energyKey(PKMaterials material) {
        return "energy.projectk." + material.energyIdPath();
    }

    private String energyNameWithReset(JsonObject lang, PKMaterials material) {
        String key = energyKey(material);
        String value = lang.has(key) ? lang.get(key).getAsString() : material.enName();
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.contains("§") ? value + "§r" : value;
    }
}
