package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.recipe.AbyssMagicTableRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class AbyssMagicTableDisplay implements Display {
    public static final CategoryIdentifier<AbyssMagicTableDisplay> ID = CategoryIdentifier.of("projectk", "abyss_magic_table");

    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final ResourceLocation energyId;
    private final long energyAmount;

    public AbyssMagicTableDisplay(AbyssMagicTableRecipe recipe) {
        this.inputs = List.of(toEntry(recipe.input()));
        this.outputs = List.of(EntryIngredients.ofItemStacks(List.of(recipe.result().copy())));
        this.energyId = recipe.energyId();
        this.energyAmount = recipe.energyAmount();
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

    public ResourceLocation getEnergyId() {
        return energyId;
    }

    public long getEnergyAmount() {
        return energyAmount;
    }
}
