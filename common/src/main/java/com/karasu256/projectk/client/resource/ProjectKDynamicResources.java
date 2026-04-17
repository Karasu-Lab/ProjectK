package com.karasu256.projectk.client.resource;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.client.resource.block.AbyssFluidGenerator;
import com.karasu256.projectk.client.resource.block.PolishedNetherrackGenerator;
import com.karasu256.projectk.client.resource.impl.AbstractProjectKDynamicResources;
import com.karasu256.projectk.client.resource.item.AbyssBucketGenerator;
import com.karasu256.projectk.client.resource.item.AbyssIngotGenerator;
import com.karasu256.projectk.client.resource.item.KarasiumDustGenerator;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;

import java.util.Collection;
import java.util.List;

public class ProjectKDynamicResources extends AbstractProjectKDynamicResources {
    public static final ProjectKDynamicResources INSTANCE = new ProjectKDynamicResources();

    private ProjectKDynamicResources() {
        super(Id.id("dynamic_resources"), PackGenerationStrategy.REGEN_ON_EVERY_RELOAD);
    }

    public static void init() {
        RegHelper.registerDynamicResourceProvider(INSTANCE);
    }

    @Override
    protected void addResources() {
        addGenerator(new AbyssIngotGenerator());
        addGenerator(new AbyssBucketGenerator());
        addGenerator(new AbyssFluidGenerator());
        addGenerator(new PolishedNetherrackGenerator());
        addGenerator(new KarasiumDustGenerator());
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return List.of(ProjectK.MOD_ID, "minecraft");
    }

    @Override
    protected void addDynamicTranslations(AfterLanguageLoadEvent event) {

    }
}