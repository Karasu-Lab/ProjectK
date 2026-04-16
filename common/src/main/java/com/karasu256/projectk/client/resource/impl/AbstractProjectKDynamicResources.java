package com.karasu256.projectk.client.resource.impl;

import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicClientResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractProjectKDynamicResources extends DynamicClientResourceProvider {
    private List<IResourceGenerator> generators;

    protected AbstractProjectKDynamicResources(ResourceLocation name, PackGenerationStrategy generationPolicy) {
        super(name, generationPolicy);
        this.generators = new ArrayList<>();
        addResources();
        this.generators = Collections.unmodifiableList(generators);
    }

    protected abstract void addResources();


    public final void addGenerator(IResourceGenerator generator) {
        generators.add(generator);
    }

    @Override
    protected void regenerateDynamicAssets(Consumer<ResourceGenTask> consumer) {
        for (IResourceGenerator generator : generators) {
            consumer.accept(generator::generate);
        }
    }
}
