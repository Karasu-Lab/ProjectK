package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.recipe.ProjectKRecipes;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

public class ProjectKREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WitherRoseCategory());
        registry.addWorkstations(WitherRoseCategory.ID, EntryStacks.of(Blocks.FIRE), EntryStacks.of(Blocks.SOUL_FIRE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerDisplayGenerator(WitherRoseCategory.ID, new DynamicDisplayGenerator<>() {
            @Override
            public Optional<List<WitherRoseDisplay>> generate(ViewSearchBuilder builder) {
                var level = Minecraft.getInstance().level;
                if (level == null) return Optional.empty();
                List<WitherRoseDisplay> displays = level.getRecipeManager().getAllRecipesFor(ProjectKRecipes.IN_BIOME_IN_BLOCK_CRAFTING.get()).stream().map(holder -> new WitherRoseDisplay(holder.value())).toList();
                if (displays.isEmpty()) return Optional.empty();
                return Optional.of(displays);
            }
        });
    }
}
