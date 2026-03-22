package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.item.custom.ProjectKItem;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.data.CustomConversionProvider;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ProjectEConversionProvider extends CustomConversionProvider {

    public ProjectEConversionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, MOD_ID);
    }

    @Override
    protected void addCustomConversions(@NotNull HolderLookup.Provider registries) {
        CustomConversionBuilder builder = createConversionBuilder(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"));

        registries.lookupOrThrow(Registries.ITEM).listElements().forEach(h -> {
            Item item = h.value();
            if (item instanceof ProjectKItem kItem) {
                kItem.getEMC().ifPresent(emcData -> {
                    builder.before(NSSItem.createItem(item), emcData.emc().longValue());
                });
            }
        });
    }
}