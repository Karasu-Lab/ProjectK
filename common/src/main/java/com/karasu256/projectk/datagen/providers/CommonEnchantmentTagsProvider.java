package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.enchant.ProjectKEnchantments;
import com.karasu256.projectk.registry.ProjectKTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

public class CommonEnchantmentTagsProvider extends TagsProvider<Enchantment> {
    public CommonEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ENCHANTMENT, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ProjectKTags.Enchantments.ABYSS_ENCHANT)
                .add(ProjectKEnchantments.ABYSS_BOOSTER_KEY);
    }
}
