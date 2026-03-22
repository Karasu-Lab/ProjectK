package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.item.ProjectKItems;
import moze_intel.projecte.api.data.CustomConversionProvider;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ProjectEConversionProvider extends CustomConversionProvider {

    public ProjectEConversionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, MOD_ID);
    }

    @Override
    protected void addCustomConversions(@NotNull HolderLookup.Provider registries) {
        createConversionBuilder(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"))
                .before(NSSItem.createItem(ProjectKItems.KARASIUM.get()), 128L);
    }
}