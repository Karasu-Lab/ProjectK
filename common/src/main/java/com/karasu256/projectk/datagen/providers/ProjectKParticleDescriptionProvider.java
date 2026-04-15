package com.karasu256.projectk.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProjectKParticleDescriptionProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public ProjectKParticleDescriptionProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "particles");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(writeParticle(output, "abyss", List.of(ProjectK.MOD_ID + ":abyss")));
        futures.add(writeParticle(output, "abyss_portal", List.of(ProjectK.MOD_ID + ":abyss_portal")));
        futures.add(writeParticle(output, "abyss_burst", List.of(ProjectK.MOD_ID + ":abyss_burst")));
        futures.add(writeParticle(output, "abyss_burst_residual", List.of(ProjectK.MOD_ID + ":abyss_burst")));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> writeParticle(CachedOutput output, String name, List<String> textures) {
        JsonObject json = new JsonObject();
        JsonArray texturesArray = new JsonArray();
        for (String texture : textures) {
            texturesArray.add(texture);
        }
        json.add("textures", texturesArray);

        Path file = pathProvider.json(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, name));
        return DataProvider.saveStable(output, json, file);
    }

    @Override
    public String getName() {
        return "ProjectK Particle Descriptions";
    }
}
