package com.karasu256.projectk.client.resource.block;

import com.google.gson.JsonObject;
import com.karasu256.projectk.client.resource.impl.AbstractResourceGenerator;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AbyssFluidGenerator extends AbstractResourceGenerator {
    @Override
    public void generate(ResourceManager manager, ResourceSink sink) {
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
}
