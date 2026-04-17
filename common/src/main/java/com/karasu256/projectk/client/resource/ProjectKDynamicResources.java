package com.karasu256.projectk.client.resource;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.client.resource.block.AbyssFluidGenerator;
import com.karasu256.projectk.client.resource.block.PolishedBlockResourceGenerator;
import com.karasu256.projectk.client.resource.impl.AbstractProjectKDynamicResources;
import com.karasu256.projectk.client.resource.item.AbyssBucketGenerator;
import com.karasu256.projectk.client.resource.item.AbyssIngotGenerator;
import com.karasu256.projectk.client.resource.item.DustResourceGenerator;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.minecraft.resources.ResourceLocation;

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
        addGenerator(new PolishedBlockResourceGenerator(ResourceLocation.withDefaultNamespace("block/netherrack"),
                Id.id("block/polished_netherrack")));
        addGenerator(new DustResourceGenerator(Id.id("item/karasium_dust"), Id.id("karasium_dust"), 0xADFF2F));
    }

    @Override
    protected void addDynamicTranslations(AfterLanguageLoadEvent event) {

    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return List.of(ProjectK.MOD_ID, "minecraft");
    }
}