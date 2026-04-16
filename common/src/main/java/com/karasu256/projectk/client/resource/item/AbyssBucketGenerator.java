package com.karasu256.projectk.client.resource.item;

import com.google.gson.JsonObject;
import com.karasu256.projectk.client.resource.impl.AbstractResourceGenerator;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AbyssBucketGenerator extends AbstractResourceGenerator {
    @Override
    public void generate(ResourceManager manager, ResourceSink sink) {
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
}
