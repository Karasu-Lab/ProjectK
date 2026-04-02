package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProjectKBlockModelProvider implements DataProvider {
    private static final double UV_SIZE = 16.0;
    private static final double TEXTURE_SIZE = 32.0;
    private static final double BORDER_SIZE = 6.0;
    private final PathProvider modelPathProvider;

    public ProjectKBlockModelProvider(PackOutput output) {
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(writeModel(output, "block/abyss_magic_table", abyssMagicTableModel()));
        futures.add(writeModel(output, "item/abyss_magic_table", abyssMagicTableItemModel()));
        futures.add(writeModel(output, "block/abyss_energy_cable", cableModel("abyss_energy_cable")));
        futures.add(writeModel(output, "block/abyss_energy_cable_center", cableCenterModel("abyss_energy_cable")));
        futures.add(writeModel(output, "block/abyss_energy_cable_side", cableSideModel("abyss_energy_cable")));

        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            String fluidId = "fluid_" + definition.idPath();
            futures.add(writeModel(output, "block/" + fluidId, fluidModel(fluidId)));

            String coreId = definition.idPath().replace("_energy", "_core");
            futures.add(writeModel(output, "block/" + coreId, coreModel()));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ProjectK Block Models";
    }

    private CompletableFuture<?> writeModel(CachedOutput output, String path, JsonObject json) {
        Path file = modelPathProvider.json(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, path));
        return DataProvider.saveStable(output, json, file);
    }

    private JsonObject abyssMagicTableModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/enchanting_table");
        return json;
    }

    private JsonObject abyssMagicTableItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_magic_table");
        return json;
    }

    private JsonObject cableModel(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/" + id + "_center");
        return json;
    }

    private JsonObject cableCenterModel(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", ProjectK.MOD_ID + ":block/multipart/" + id);
        textures.addProperty("all", ProjectK.MOD_ID + ":block/multipart/" + id);
        json.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(cubeElement(new double[]{5.0, 5.0, 5.0}, new double[]{11.0, 11.0, 11.0}, fullUv()));
        json.add("elements", elements);
        return json;
    }

    private JsonObject cableSideModel(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", ProjectK.MOD_ID + ":block/multipart/" + id + "_vertical");
        textures.addProperty("all", ProjectK.MOD_ID + ":block/multipart/" + id + "_vertical");
        json.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(cubeElement(new double[]{5.0, 5.0, 0.0}, new double[]{11.0, 11.0, 5.0}, new double[]{0.0, 0.0, 16.0, 16.0}));
        json.add("elements", elements);
        return json;
    }

    private JsonObject fluidModel(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/water");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", ProjectK.MOD_ID + ":block/" + id + "_still");
        textures.addProperty("still", ProjectK.MOD_ID + ":block/" + id + "_still");
        textures.addProperty("flow", ProjectK.MOD_ID + ":block/" + id + "_flow");
        json.add("textures", textures);
        return json;
    }

    private JsonObject coreModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", ProjectK.MOD_ID + ":block/abyss_core");
        json.add("textures", textures);
        return json;
    }

    private JsonObject cubeElement(double[] from, double[] to, double[] uv) {
        JsonObject element = new JsonObject();
        element.add("from", array(from));
        element.add("to", array(to));
        JsonObject faces = new JsonObject();
        faces.add("north", texturedFace(uv));
        faces.add("south", texturedFace(uv));
        faces.add("east", texturedFace(uv));
        faces.add("west", texturedFace(uv));
        faces.add("up", texturedFace(uv));
        faces.add("down", texturedFace(uv));
        element.add("faces", faces);
        return element;
    }

    private JsonObject texturedFace(double[] uv) {
        JsonObject face = new JsonObject();
        face.add("uv", array(uv));
        face.addProperty("texture", "#all");
        return face;
    }

    private double[] fullUv() {
        return new double[]{0.0, 0.0, UV_SIZE, UV_SIZE};
    }

    private JsonArray array(double[] values) {
        JsonArray array = new JsonArray();
        for (double value : values) {
            array.add(value);
        }
        return array;
    }
}
