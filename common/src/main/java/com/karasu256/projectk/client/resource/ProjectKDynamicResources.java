package com.karasu256.projectk.client.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicClientResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ProjectKDynamicResources extends DynamicClientResourceProvider {
    public static final ProjectKDynamicResources INSTANCE = new ProjectKDynamicResources();

    private ProjectKDynamicResources() {
        super(Id.id("dynamic_resources"), PackGenerationStrategy.REGEN_ON_EVERY_RELOAD);
    }

    public static void init() {
        RegHelper.registerDynamicResourceProvider(INSTANCE);
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return List.of(ProjectK.MOD_ID, "minecraft");
    }

    @Override
    protected void addDynamicTranslations(AfterLanguageLoadEvent event) {
    }

    @Override
    protected void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept(this::generateAbyssIngots);
        executor.accept(this::generateAbyssBuckets);
        executor.accept(this::generateAbyssFluids);
        executor.accept(this::generatePolishedNetherrack);
    }

    private void generatePolishedNetherrack(ResourceManager manager, ResourceSink sink) {
        ResourceLocation netherrackId = ResourceLocation.withDefaultNamespace("block/netherrack");
        ResourceLocation templateId = ResourceLocation.withDefaultNamespace("block/polished_andesite");
        ResourceLocation textureId = Id.id("block/polished_netherrack");

        try (TextureImage netherrack = TextureImage.open(manager,
                netherrackId); TextureImage template = TextureImage.open(manager, templateId)) {

            TextureOps.grayscale(template);

            Palette palette = Palette.fromImage(netherrack);
            try (TextureImage recolored = Respriter.of(template).recolor(palette)) {
                sink.addTexture(textureId, recolored);
            }

        } catch (Exception ignored) {
        }
    }

    private void generateAbyssIngots(ResourceManager manager, ResourceSink sink) {
        List<ProjectKEnergies.EnergyDefinition> definitions = ProjectKEnergies.getDefinitions();
        for (ProjectKEnergies.EnergyDefinition energy : definitions) {
            String energyPath = energy.idPath();
            ResourceLocation textureId = Id.id("item/abyss_ingot_" + energyPath);
            ResourceLocation modelId = Id.id("abyss_ingot_" + energyPath);
            try (TextureImage texture = TextureImage.open(manager, Id.id("item/abyss_ingot"))) {
                tint(texture, energy.color());
                sink.addTexture(textureId, texture);
                sink.addItemModel(modelId, createGeneratedModel(textureId));
            } catch (Exception ignored) {
            }
        }

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "projectk:item/abyss_ingot");
        model.add("textures", textures);

        JsonArray overrides = new JsonArray();
        for (ProjectKEnergies.EnergyDefinition energy : definitions) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("projectk:abyss_energy", ProjectKEnergies.getModelPredicateValue(energy.id()));
            override.add("predicate", predicate);
            override.addProperty("model", "projectk:item/abyss_ingot_" + energy.idPath());
            overrides.add(override);
        }
        model.add("overrides", overrides);
        sink.addItemModel(Id.id("abyss_ingot"), model);
    }

    private void generateAbyssBuckets(ResourceManager manager, ResourceSink sink) {
        for (ProjectKEnergies.EnergyDefinition energy : ProjectKEnergies.getDefinitions()) {
            String energyPath = energy.idPath();
            ResourceLocation contentTextureId = Id.id("item/bucket_content_" + energyPath);
            ResourceLocation modelId = Id.id("bucket_of_" + energyPath);
            try (TextureImage content = TextureImage.open(manager, Id.id("item/bucket_of_abyss_enrgy_fluid"))) {
                TextureOps.grayscale(content);
                tint(content, energy.color());
                sink.addTexture(contentTextureId, content);

                JsonObject model = new JsonObject();
                model.addProperty("parent", "minecraft:item/generated");
                JsonObject textures = new JsonObject();
                textures.addProperty("layer0", "minecraft:item/bucket");
                textures.addProperty("layer1", contentTextureId.toString());
                model.add("textures", textures);
                sink.addItemModel(modelId, model);
            } catch (Exception ignored) {
            }
        }
    }

    private void generateAbyssFluids(ResourceManager manager, ResourceSink sink) {
        for (ProjectKEnergies.EnergyDefinition energy : ProjectKEnergies.getDefinitions()) {
            String energyPath = energy.idPath();
            ResourceLocation stillId = Id.id("block/fluid_" + energyPath + "_still");
            ResourceLocation flowId = Id.id("block/fluid_" + energyPath + "_flow");
            try (TextureImage still = TextureImage.open(manager,
                    Id.id("block/base_fluid_still")); TextureImage flow = TextureImage.open(manager,
                    Id.id("block/base_fluid_flow"))) {
                TextureOps.grayscale(still);
                TextureOps.grayscale(flow);
                tint(still, energy.color());
                tint(flow, energy.color());
                sink.addTexture(stillId, still);
                sink.addTexture(flowId, flow);

                ResourceLocation blockModelId = Id.id("fluid_" + energyPath);
                JsonObject blockModel = new JsonObject();
                blockModel.addProperty("loader", "neoforge:fluid");
                blockModel.addProperty("fluid", "projectk:fluid_" + energyPath);
                sink.addBlockModel(blockModelId, blockModel);
            } catch (Exception ignored) {
            }
        }
    }

    private void tint(TextureImage img, int tint) {
        int targetR = (tint >> 16) & 0xFF;
        int targetG = (tint >> 8) & 0xFF;
        int targetB = tint & 0xFF;

        img.forEachPixel(pixel -> {
            int color = pixel.getValue();
            int alpha = FastColor.ABGR32.alpha(color);
            if (alpha > 0) {
                int r = (FastColor.ABGR32.red(color) * targetR) / 255;
                int g = (FastColor.ABGR32.green(color) * targetG) / 255;
                int b = (FastColor.ABGR32.blue(color) * targetB) / 255;
                pixel.setValue(FastColor.ABGR32.color(alpha, b, g, r));
            }
        });
    }

    private JsonObject createGeneratedModel(ResourceLocation texture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texture.toString());
        model.add("textures", textures);
        return model;
    }
}