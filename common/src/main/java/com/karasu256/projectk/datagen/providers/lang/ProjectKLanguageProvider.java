package com.karasu256.projectk.datagen.providers.lang;

import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProjectKLanguageProvider implements DataProvider {
    private final PathProvider langPathProvider;

    public ProjectKLanguageProvider(PackOutput output) {
        this.langPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<ProjectKLanguage> languages = List.of(new EnUsLanguage(), new JaJpLanguage());
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (ProjectKLanguage language : languages) {
            JsonObject json = new JsonObject();
            TranslationAdder adder = (key, value) -> json.addProperty(key, value);

            language.addCommon(adder);
            addBlockSetTranslations(language, json, adder);
            addEnergyTranslations(language, json, adder);
            addEnergyItemTranslations(language, json, adder);
            addEnergyBlockTranslations(language, json, adder);

            futures.add(writeLang(output, language.getLocale(), json));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void addBlockSetTranslations(ProjectKLanguage lang, JsonObject json, TranslationAdder adder) {
        ProjectKBlocks.getBlockSets().forEach((fullBlockSupplier, set) -> {
            Block fullBlock = fullBlockSupplier.get();
            String fullKey = fullBlock.getDescriptionId();

            if (!json.has(fullKey)) return;
            String baseName = json.get(fullKey).getAsString();

            set.forEach((material, blockSupplier) -> {
                if (material == BlockMaterials.FULL) return;
                lang.addBlockSet(baseName, material, blockSupplier.get().getDescriptionId(), adder);
            });
        });
    }

    private void addEnergyTranslations(ProjectKLanguage language, JsonObject json, TranslationAdder adder) {
        ProjectKEnergies.getDefinitions().forEach(definition -> {
            language.addEnergy(definition, (key, value) -> {
                if (key.contains("energy_type.abyss")) {
                    if (definition.id().equals(ProjectKEnergies.BASE_ID)) {
                        adder.add(key, value);
                    }
                } else {
                    adder.add(key, value);
                }
            });
        });
    }

    private void addEnergyItemTranslations(ProjectKLanguage lang, JsonObject json, TranslationAdder adder) {
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            lang.addEnergyItem(definition, resolveEnergyName(json, definition), adder);
        }
    }

    private void addEnergyBlockTranslations(ProjectKLanguage lang, JsonObject json, TranslationAdder adder) {
        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            lang.addEnergyBlock(definition, resolveEnergyName(json, definition), adder);
        }
    }

    private String resolveEnergyName(JsonObject json, ProjectKEnergies.EnergyDefinition definition) {
        String key = "energy.projectk." + definition.idPath();
        String value = json.has(key) ? json.get(key).getAsString() : "";
        if (value.isEmpty()) return "";
        return value.contains("§") ? value + "§r" : value;
    }

    @Override
    public String getName() {
        return "ProjectK Languages";
    }

    private CompletableFuture<?> writeLang(CachedOutput output, String locale, JsonObject json) {
        Path file = langPathProvider.json(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, locale));
        return DataProvider.saveStable(output, json, file);
    }
}
