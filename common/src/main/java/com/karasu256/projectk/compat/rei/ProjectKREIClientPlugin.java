package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.recipe.ProjectKRecipes;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ProjectKREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        var level = Minecraft.getInstance().level;
        var connection = Minecraft.getInstance().getConnection();
        var manager = level != null ? level.getRecipeManager() : connection != null ? connection.getRecipeManager() : null;
        if (manager == null) return;

        var recipes = manager.getAllRecipesFor(ProjectKRecipes.IN_BIOME_IN_BLOCK_CRAFTING.get());
        var seen = new HashSet<CategoryIdentifier<InBiomeInBlockCraftingDisplay>>();
        for (var holder : recipes) {
            var recipe = holder.value();
            var categoryId = InBiomeInBlockCraftingDisplay.categoryIdFor(recipe.biome());
            if (!seen.add(categoryId)) continue;
            var iconId = new InBiomeInBlockCraftingDisplay(recipe).getBiomeIconId();
            var title = Component.translatable("rei.category.projectk.in_biome_in_block_crafting").append(Component.literal(" ")).append(InBiomeInBlockCraftingDisplay.biomeName(recipe.biome()));
            registry.add(new InBiomeInBlockCraftingCategory(categoryId, iconId, title));
            registry.addWorkstations(categoryId, EntryStacks.of(Blocks.FIRE), EntryStacks.of(Blocks.SOUL_FIRE));
        }
    }

    @Override
    public void registerDisplays(@NotNull DisplayRegistry registry) {
        registry.registerGlobalDisplayGenerator(new DynamicDisplayGenerator<>() {
            @Override
            public Optional<List<Display>> generate(ViewSearchBuilder builder) {
                var level = Minecraft.getInstance().level;
                if (level == null) return Optional.empty();
                List<Display> displays = level.getRecipeManager().getAllRecipesFor(ProjectKRecipes.IN_BIOME_IN_BLOCK_CRAFTING.get()).stream().map(holder -> (Display) new InBiomeInBlockCraftingDisplay(holder.value())).toList();
                if (displays.isEmpty()) return Optional.empty();
                return Optional.of(displays);
            }
        });
    }
}
