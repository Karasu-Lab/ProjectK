package com.karasu256.projectk.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.level.block.Blocks;

public class ProjectKREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WitherRoseCategory());
        registry.addWorkstations(WitherRoseCategory.ID, EntryStacks.of(Blocks.FIRE), EntryStacks.of(Blocks.SOUL_FIRE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.add(new WitherRoseDisplay());
    }
}
