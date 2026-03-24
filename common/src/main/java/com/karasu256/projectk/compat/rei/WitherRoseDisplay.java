package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.item.ProjectKItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.Items;

import java.util.List;

public class WitherRoseDisplay implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(EntryIngredients.of(ProjectKItems.WITHER_BONE.get()), EntryIngredients.of(Items.POPPY));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(Items.WITHER_ROSE));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return WitherRoseCategory.ID;
    }
}
