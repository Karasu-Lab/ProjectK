package com.karasu256.projectk.api.datagen.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public abstract class AbstractParticleDescriptionProvider extends AbstractListDataProvider<ResourceLocation> {
    public AbstractParticleDescriptionProvider(PackOutput output, String name) {
        super(output, PackOutput.Target.RESOURCE_PACK, name);
    }

    public void addParticle(RegistrySupplier<? extends ParticleType<?>> particle) {
        addParticle(particle, particle.getId());
    }

    public void addParticle(RegistrySupplier<? extends ParticleType<?>> particle, ResourceLocation... textures) {
        addData(particle.getId().getPath(), List.of(textures));
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
