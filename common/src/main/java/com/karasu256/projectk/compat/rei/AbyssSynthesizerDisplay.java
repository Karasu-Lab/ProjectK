package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.recipe.AbyssSynthesizerRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbyssSynthesizerDisplay implements Display {
    public static final CategoryIdentifier<AbyssSynthesizerDisplay> ID = CategoryIdentifier.of("projectk",
            "abyss_synthesizer");

    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final List<AbyssEnergyData> energies;

    public AbyssSynthesizerDisplay(AbyssSynthesizerRecipe recipe) {
        List<EntryIngredient> inputList = new ArrayList<>();
        for (IngredientStack stack : recipe.inputs()) {
            inputList.add(toEntry(stack));
        }
        this.inputs = inputList;
        this.outputs = List.of(EntryIngredients.ofItemStacks(List.of(recipe.result().copy())));
        this.energies = recipe.energies();
    }

    private static EntryIngredient toEntry(IngredientStack requirement) {
        ItemStack[] items = requirement.ingredient().getItems();
        if (items.length == 0) {
            return EntryIngredient.empty();
        }
        List<ItemStack> stacks = Arrays.stream(items).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(requirement.count());
            return copy;
        }).toList();
        if (stacks.isEmpty()) {
            return EntryIngredient.empty();
        }
        return EntryIngredients.ofItemStacks(stacks);
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
        return ID;
    }

    public List<AbyssEnergyData> getEnergies() {
        return energies;
    }
}
