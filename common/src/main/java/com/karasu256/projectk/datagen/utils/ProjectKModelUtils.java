package com.karasu256.projectk.datagen.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ProjectKModelUtils {

    public static JsonObject simpleItemModel(String texturePath) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texturePath);
        json.add("textures", textures);
        return json;
    }

    public static JsonObject bucketModel(String fluidId, String contentTexturePath) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/generated");
        json.addProperty("loader", "neoforge:fluid_container");
        json.addProperty("fluid", fluidId);
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "minecraft:item/bucket");
        textures.addProperty("layer1", contentTexturePath);
        textures.addProperty("base", "minecraft:item/bucket");
        textures.addProperty("fluid", contentTexturePath);
        json.add("textures", textures);
        return json;
    }

    public static JsonObject itemModelWithOverrides(String baseTexture, JsonArray overrides) {
        JsonObject json = simpleItemModel(baseTexture);
        json.add("overrides", overrides);
        return json;
    }

    public static JsonObject itemOverride(String predicateId, float predicateValue, String modelPath) {
        JsonObject json = new JsonObject();
        JsonObject predicate = new JsonObject();
        predicate.addProperty(predicateId, predicateValue);
        json.add("predicate", predicate);
        json.addProperty("model", modelPath);
        return json;
    }
}
