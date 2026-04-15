package com.karasu256.projectk.api.datagen.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public abstract class AbstractParticleDescriptionProvider extends AbstractListDataProvider<ResourceLocation> {
    public AbstractParticleDescriptionProvider(PackOutput output, String name) {
        super(output, name);
    }

    public void addParticle(RegistrySupplier<? extends ParticleType<? extends ParticleOptions>> particle) {
//        futures.add(writeParticle(output, "abyss", List.of(ProjectK.MOD_ID + ":abyss")));
//        futures.add(writeParticle(output, "abyss_portal", List.of(ProjectK.MOD_ID + ":abyss_portal")));
//        futures.add(writeParticle(output, "abyss_burst", List.of(ProjectK.MOD_ID + ":abyss_burst")));
//        futures.add(writeParticle(output, "abyss_burst_residual", List.of(ProjectK.MOD_ID + ":abyss_burst")));
    }

    @Override
    public JsonElement getElement(List<ResourceLocation> data) {
        JsonObject json = new JsonObject();
        JsonArray texturesArray = new JsonArray();
        for (var texture : data) {
            texturesArray.add(texture.toString());
        }
        json.add("textures", texturesArray);
        return json;
    }
}
