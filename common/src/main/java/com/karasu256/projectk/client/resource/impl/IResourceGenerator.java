package com.karasu256.projectk.client.resource.impl;

import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.minecraft.server.packs.resources.ResourceManager;

public interface IResourceGenerator {
    void generate(ResourceManager manager, ResourceSink sink);
}
