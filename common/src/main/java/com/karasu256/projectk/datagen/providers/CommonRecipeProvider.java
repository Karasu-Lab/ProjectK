package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.item.ProjectKItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommonRecipeProvider extends RecipeProvider {
    public CommonRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void generate(RecipeOutput output) {
        oreSmelting(output, List.of(ProjectKItems.RAW_KARASIUM.get()), RecipeCategory.MISC, ProjectKItems.KARASIUM.get(), 0.7f, 200, "karasium");
        oreBlasting(output, List.of(ProjectKItems.RAW_KARASIUM.get()), RecipeCategory.MISC, ProjectKItems.KARASIUM.get(), 0.7f, 100, "karasium");
        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, com.karasu256.projectk.block.ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', com.karasu256.projectk.item.ProjectKItems.ABYSS_ABSORPTION_PRISM_SHARD.get())
                .unlockedBy("has_shard", has(com.karasu256.projectk.item.ProjectKItems.ABYSS_ABSORPTION_PRISM_SHARD.get()))
                .save(output);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        generate(output);
    }
}
