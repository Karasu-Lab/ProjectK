package com.karasu256.projectk.client.resource.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karasu256.projectk.client.resource.impl.AbstractResourceGenerator;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

public class AbyssIngotGenerator extends AbstractResourceGenerator {
    @Override
    public void generate(ResourceManager manager, ResourceSink sink) {
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
}
