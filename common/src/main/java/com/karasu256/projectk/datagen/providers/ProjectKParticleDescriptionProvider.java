package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.api.datagen.impl.AbstractParticleDescriptionProvider;
import com.karasu256.projectk.particle.ProjectKParticles;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ProjectKParticleDescriptionProvider extends AbstractParticleDescriptionProvider {
    public ProjectKParticleDescriptionProvider(PackOutput output) {
        super(output, "particles");
    }

    @Override
    public CompletableFuture<?> runDataGen(CachedOutput cachedOutput) {
        addParticle(ProjectKParticles.ABYSS_PARTICLE);
        addParticle(ProjectKParticles.ABYSS_PORTAL_PARTICLE);
        addParticle(ProjectKParticles.ABYSS_BURST_PARTICLE);
        addParticle(ProjectKParticles.ABYSS_LASER_PARTICLE);
        addParticle(ProjectKParticles.ABYSS_BURST_RESIDUAL_PARTICLE);
        return super.runDataGen(cachedOutput);
    }
}
