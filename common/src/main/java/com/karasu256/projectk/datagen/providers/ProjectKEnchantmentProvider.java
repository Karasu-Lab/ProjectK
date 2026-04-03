package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonObject;
import com.karasu256.projectk.utils.Id;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProjectKEnchantmentProvider implements DataProvider {
    private final PathProvider pathProvider;

    public ProjectKEnchantmentProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "enchantment");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<String, JsonObject> definitions = new LinkedHashMap<>();
        register(definitions);
        CompletableFuture<?>[] writes = definitions.entrySet().stream().map(entry -> write(output, entry.getKey(), entry.getValue())).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(writes);
    }

    @Override
    public String getName() {
        return "ProjectK Enchantments";
    }

    private CompletableFuture<?> write(CachedOutput output, String id, JsonObject json) {
        Path path = pathProvider.json(Id.id(id));
        return DataProvider.saveStable(output, json, path);
    }

    private void register(Map<String, JsonObject> definitions) {
        definitions.put("abyss_booster", simpleEnchantment("enchantment.projectk.abyss_booster", "#minecraft:swords", 30, 1, 10, 1, 15, 5, 10, "mainhand"));
    }

    private JsonObject simpleEnchantment(String translationKey, String supportedItems, int maxLevel, int minCostBase, int minCostPerLevel, int maxCostBase, int maxCostPerLevel, int anvilCost, int weight, String... slots) {
        JsonObject root = new JsonObject();
        root.addProperty("anvil_cost", anvilCost);

        JsonObject description = new JsonObject();
        description.addProperty("translate", translationKey);
        root.add("description", description);

        JsonObject maxCost = new JsonObject();
        maxCost.addProperty("base", maxCostBase);
        maxCost.addProperty("per_level_above_first", maxCostPerLevel);
        root.add("max_cost", maxCost);

        root.addProperty("max_level", maxLevel);

        JsonObject minCost = new JsonObject();
        minCost.addProperty("base", minCostBase);
        minCost.addProperty("per_level_above_first", minCostPerLevel);
        root.add("min_cost", minCost);

        root.add("slots", JsonUtil.stringArray(slots));
        root.addProperty("supported_items", supportedItems);
        root.addProperty("weight", weight);
        return root;
    }

    private static final class JsonUtil {
        private static com.google.gson.JsonArray stringArray(String... values) {
            com.google.gson.JsonArray array = new com.google.gson.JsonArray();
            for (String value : values) {
                array.add(value);
            }
            return array;
        }
    }
}
