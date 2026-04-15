package com.karasu256.projectk.api.datagen.impl;

import net.minecraft.client.particle.Particle;
import net.minecraft.data.PackOutput;

public abstract class AbstractParticleDescriptionProvider extends AbstractProjectKDataProvider<Particle> {
    public AbstractParticleDescriptionProvider(PackOutput output, String name) {
        super(output, name);
    }
}
