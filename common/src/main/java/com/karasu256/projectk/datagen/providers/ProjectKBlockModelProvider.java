package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.energy.PKMaterials;
import com.karasu256.projectk.datagen.utils.ProjectKModelUtils;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.registry.ItemsRegistry;
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
    private static final String ABYSS_ENERGY_PROPERTY = ProjectK.MOD_ID + ":abyss_energy";
    private final PathProvider modelPathProvider;

    public ProjectKBlockModelProvider(PackOutput output) {
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(writeModel(output, "block/abyss_magic_table", abyssMagicTableModel()));
        futures.add(writeModel(output, "item/abyss_magic_table", abyssMagicTableItemModel()));
        futures.add(writeModel(output, "block/abyss_alchemy_blend_machine", abyssAlchemyBlendMachineModel()));
        futures.add(writeModel(output, "item/abyss_alchemy_blend_machine", abyssAlchemyBlendMachineItemModel()));
        futures.add(writeModel(output, "block/abyss_enchanter", abyssEnchanterModel()));
        futures.add(writeModel(output, "item/abyss_enchanter", abyssEnchanterItemModel()));
        futures.add(writeModel(output, "block/abyss_charger", abyssChargerModel()));
        futures.add(writeModel(output, "item/abyss_charger", abyssChargerItemModel()));
        futures.add(writeModel(output, "block/abyss_storage", abyssStorageModel()));
        futures.add(writeModel(output, "item/abyss_storage", abyssStorageItemModel()));
        futures.add(writeModel(output, "block/abyss_enchant_remover", abyssEnchantRemoverModel()));
        futures.add(writeModel(output, "item/abyss_enchant_remover", abyssEnchantRemoverItemModel()));
        futures.add(writeModel(output, "block/abyss_synthesizer", abyssSynthesizerModel()));
        futures.add(writeModel(output, "item/abyss_synthesizer", abyssSynthesizerItemModel()));
        futures.add(writeModel(output, "block/abyss_energy_cable", cableModel("abyss_energy_cable")));
        futures.add(writeModel(output, "block/abyss_energy_cable_center", cableCenterModel("abyss_energy_cable")));
        futures.add(writeModel(output, "block/abyss_energy_cable_side", cableSideModel("abyss_energy_cable")));
        futures.add(writeModel(output, "block/abyss_energy_cable_side_input",
                cableSideModelWithTexture(ProjectK.MOD_ID + ":block/multipart/abyss_energy_cable_vertical")));
        futures.add(writeModel(output, "block/abyss_energy_cable_side_output",
                cableSideModelWithTexture(ProjectK.MOD_ID + ":block/multipart/abyss_energy_cable_vertical")));
        futures.add(writeModel(output, "block/abyss_portal", abyssPortalModel(false)));
        futures.add(writeModel(output, "block/abyss_portal_active", abyssPortalModel(true)));
        futures.add(writeModel(output, "item/abyss_portal", abyssPortalItemModel()));

        for (PKMaterials material : PKMaterials.values()) {
            ProjectKEnergies.EnergyDefinition definition = ProjectKEnergies.getByMaterial(material);
            String fluidId = "fluid_" + definition.idPath();
            futures.add(writeModel(output, "block/" + fluidId, fluidModel(fluidId)));

            String coreId = definition.idPath().replace("_energy", "_core");
            futures.add(writeModel(output, "block/" + coreId, coreModel()));

            String bucketPath = "item/bucket_of_" + definition.idPath();
            futures.add(writeModel(output, bucketPath,
                    ProjectKModelUtils.bucketModel(ProjectK.MOD_ID + ":" + fluidId, ProjectK.MOD_ID + ":item/bucket_of_abyss_enrgy_fluid")));
        }

        for (ResourceLocation itemId : ItemsRegistry.getEnergySuffixItems()) {
            JsonArray overrides = new JsonArray();
            for (PKMaterials material : PKMaterials.values()) {
                ProjectKEnergies.EnergyDefinition definition = ProjectKEnergies.getByMaterial(material);
                String suffix = energySuffix(definition.id());
                String modelPath = "item/" + itemId.getPath() + "_" + suffix;
                futures.add(writeModel(output, modelPath,
                        ProjectKModelUtils.simpleItemModel(ProjectK.MOD_ID + ":item/abyss_ingot")));
                overrides.add(
                        ProjectKModelUtils.itemOverride(ABYSS_ENERGY_PROPERTY,
                                ProjectKEnergies.getModelPredicateValue(definition.id()),
                                ProjectK.MOD_ID + ":" + modelPath));
            }
            String baseTexture = ProjectK.MOD_ID + ":item/" + itemId.getPath();
            futures.add(writeModel(output, "item/" + itemId.getPath(),
                    ProjectKModelUtils.itemModelWithOverrides(baseTexture, overrides)));
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
        json.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", ProjectK.MOD_ID + ":block/abyss_magic_table");
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssMagicTableItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_magic_table");
        return json;
    }

    private JsonObject abyssAlchemyBlendMachineModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_bottom_top");
        JsonObject textures = new JsonObject();
        textures.addProperty("side", ProjectK.MOD_ID + ":block/abyss_alchemy_blend_machine_side");
        textures.addProperty("bottom", ProjectK.MOD_ID + ":block/abyss_alchemy_blend_machine_bottom");
        textures.addProperty("top", ProjectK.MOD_ID + ":block/abyss_alchemy_blend_machine_top");
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssAlchemyBlendMachineItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_alchemy_blend_machine");
        return json;
    }

    private JsonObject abyssEnchanterModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_bottom_top");
        JsonObject textures = new JsonObject();
        textures.addProperty("side", ProjectK.MOD_ID + ":block/abyss_enchanter_side");
        textures.addProperty("bottom", ProjectK.MOD_ID + ":block/abyss_enchanter_bottom");
        textures.addProperty("top", ProjectK.MOD_ID + ":block/abyss_enchanter_top");
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssEnchanterItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_enchanter");
        return json;
    }

    private JsonObject abyssChargerModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", ProjectK.MOD_ID + ":block/abyss_charger");
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssSynthesizerModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_bottom_top");
        JsonObject textures = new JsonObject();
        textures.addProperty("side", ProjectK.MOD_ID + ":block/abyss_synthesizer_side");
        textures.addProperty("bottom", ProjectK.MOD_ID + ":block/abyss_synthesizer_bottom");
        textures.addProperty("top", ProjectK.MOD_ID + ":block/abyss_synthesizer_top");
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssSynthesizerItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_synthesizer");
        return json;
    }

    private JsonObject abyssChargerItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_charger");
        return json;
    }

    private JsonObject abyssStorageModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", ProjectK.MOD_ID + ":block/abyss_storage");
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssStorageItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_storage");
        return json;
    }

    private JsonObject abyssEnchantRemoverModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_bottom_top");
        JsonObject textures = new JsonObject();
        textures.addProperty("side", ProjectK.MOD_ID + ":block/abyss_enchant_remover_side");
        textures.addProperty("bottom", ProjectK.MOD_ID + ":block/abyss_enchant_remover_bottom");
        textures.addProperty("top", ProjectK.MOD_ID + ":block/abyss_enchant_remover_top");
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssEnchantRemoverItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_enchant_remover");
        return json;
    }

    private JsonObject abyssPortalModel(boolean active) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", ProjectK.MOD_ID + ":block/abyss_portal" + (active ? "_active" : ""));
        json.add("textures", textures);
        return json;
    }

    private JsonObject abyssPortalItemModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", ProjectK.MOD_ID + ":block/abyss_portal");
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
        return cableSideModelWithTexture(ProjectK.MOD_ID + ":block/multipart/" + id + "_vertical");
    }

    private JsonObject cableSideModelWithTexture(String texturePath) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", texturePath);
        textures.addProperty("all", texturePath);
        json.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(cubeElement(new double[]{5.0, 5.0, 0.0}, new double[]{11.0, 11.0, 5.0},
                new double[]{0.0, 0.0, 16.0, 16.0}));
        json.add("elements", elements);
        return json;
    }

    private JsonObject fluidModel(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("loader", "neoforge:fluid");
        json.addProperty("fluid", ProjectK.MOD_ID + ":" + id);
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

    private JsonObject itemModel(String texturePath) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texturePath);
        json.add("textures", textures);
        return json;
    }

    private JsonObject itemModelWithOverrides(String texturePath, List<JsonObject> overrides) {
        JsonObject json = itemModel(texturePath);
        JsonArray array = new JsonArray();
        for (JsonObject override : overrides) {
            array.add(override);
        }
        json.add("overrides", array);
        return json;
    }

    private JsonObject itemOverride(String predicateId, float predicateValue, String modelPath) {
        JsonObject json = new JsonObject();
        JsonObject predicate = new JsonObject();
        predicate.addProperty(predicateId, predicateValue);
        json.add("predicate", predicate);
        json.addProperty("model", modelPath);
        return json;
    }

    private String energySuffix(ResourceLocation energyId) {
        return energyId.getNamespace() + "_" + energyId.getPath();
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
