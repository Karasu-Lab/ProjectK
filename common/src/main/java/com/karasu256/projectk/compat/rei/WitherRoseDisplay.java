package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.recipe.InBiomeInBlockCraftingRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WitherRoseDisplay implements Display {
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    public WitherRoseDisplay(InBiomeInBlockCraftingRecipe recipe) {
        this.inputs = recipe.inputs().stream().map(WitherRoseDisplay::toEntry).toList();
        this.outputs = List.of(EntryIngredients.ofItemStacks(List.of(recipe.result().copy())));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return WitherRoseCategory.ID;
    }

    private static EntryIngredient toEntry(IngredientStack requirement) {
        ItemStack[] items = requirement.ingredient().getItems();
        if (items.length == 0) return EntryIngredient.empty();
        List<ItemStack> stacks = Arrays.stream(items)
                .map(stack -> {
                    ItemStack copy = stack.copy();
                    copy.setCount(requirement.count());
                    return copy;
                })
                .toList();
        if (stacks.isEmpty()) return EntryIngredient.empty();
        return EntryIngredients.ofItemStacks(Collections.unmodifiableList(stacks));
    }
}
