package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ProjectKBlockModelProvider implements DataProvider {
    private final PathProvider modelPathProvider;

    public ProjectKBlockModelProvider(PackOutput output) {
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(
                writeModel(output, "block/abyss_magic_table", abyssMagicTableModel()),
                writeModel(output, "block/abyss_energy_cable", abyssEnergyCableModel()),
                writeModel(output, "block/transmitter/small/small", cableParentModel()),
                writeModel(output, "block/transmitter/small/abyss_energy_cable/basic", cableBasicModel())
        );
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

    private JsonObject abyssEnergyCableModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/transmitter/small/abyss_energy_cable/basic");
        return json;
    }

    private JsonObject cableParentModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "block/block");
        json.addProperty("loader", "mekanism:transmitter");
        json.addProperty("model", ProjectK.MOD_ID + ":models/block/transmitter_small.obj.mek");
        json.addProperty("flip_v", true);

        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "#center_down");
        textures.addProperty("center_up", "#center_down");
        textures.addProperty("center_north", "#center_down");
        textures.addProperty("center_south", "#center_down");
        textures.addProperty("center_east", "#center_down");
        textures.addProperty("center_west", "#center_down");
        json.add("textures", textures);

        JsonObject display = new JsonObject();
        display.add("gui", displayEntry(new int[]{30, 225, 0}, new double[]{0, 0, 0}, new double[]{1, 1, 1}));
        display.add("ground", displayEntry(new int[]{0, 0, 0}, new double[]{0, 3, 0}, new double[]{0.25, 0.25, 0.25}));
        display.add("fixed", displayEntry(new int[]{0, 0, 0}, new double[]{0, 0, 0}, new double[]{0.5, 0.5, 0.5}));
        display.add("thirdperson_righthand", displayEntry(new int[]{75, 45, 0}, new double[]{0, 2.5, 0}, new double[]{0.375, 0.375, 0.375}));
        display.add("firstperson_righthand", displayEntry(new int[]{0, 45, 0}, new double[]{0, 0, 0}, new double[]{0.40, 0.40, 0.40}));
        display.add("firstperson_lefthand", displayEntry(new int[]{0, 225, 0}, new double[]{0, 0, 0}, new double[]{0.40, 0.40, 0.40}));
        json.add("display", display);
        return json;
    }

    private JsonObject cableBasicModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/transmitter/small/small");

        JsonObject textures = new JsonObject();
        textures.addProperty("side", ProjectK.MOD_ID + ":block/models/multipart/abyss_energy_cable_vertical");
        textures.addProperty("center_down", ProjectK.MOD_ID + ":block/models/multipart/abyss_energy_cable");
        textures.addProperty("side_opaque", ProjectK.MOD_ID + ":block/models/multipart/opaque/abyss_energy_cable_vertical");
        textures.addProperty("center_opaque", ProjectK.MOD_ID + ":block/models/multipart/opaque/abyss_energy_cable");
        json.add("textures", textures);
        return json;
    }

    private JsonObject displayEntry(int[] rotation, double[] translation, double[] scale) {
        JsonObject entry = new JsonObject();
        entry.add("rotation", array(rotation));
        entry.add("translation", array(translation));
        entry.add("scale", array(scale));
        return entry;
    }

    private JsonArray array(double[] values) {
        JsonArray array = new JsonArray();
        for (double value : values) {
            array.add(value);
        }
        return array;
    }

    private JsonArray array(int[] values) {
        JsonArray array = new JsonArray();
        for (int value : values) {
            array.add(value);
        }
        return array;
    }
}
