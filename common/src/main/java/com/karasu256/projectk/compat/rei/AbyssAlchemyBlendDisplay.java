package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.recipe.AbyssAlchemyBlendRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class AbyssAlchemyBlendDisplay implements Display {
    public static final CategoryIdentifier<AbyssAlchemyBlendDisplay> ID = CategoryIdentifier.of("projectk", "abyss_alchemy_blend_machine");

    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final ResourceLocation energyId1;
    private final long energyAmount1;
    private final ResourceLocation energyId2;
    private final long energyAmount2;

    public AbyssAlchemyBlendDisplay(AbyssAlchemyBlendRecipe recipe) {
        this.inputs = List.of(toEntry(recipe.input()));
        this.outputs = List.of(EntryIngredients.ofItemStacks(List.of(recipe.result().copy())));
        this.energyId1 = recipe.energyId1();
        this.energyAmount1 = recipe.energyAmount1();
        this.energyId2 = recipe.energyId2();
        this.energyAmount2 = recipe.energyAmount2();
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

    public ResourceLocation getEnergyId1() {
        return energyId1;
    }

    public long getEnergyAmount1() {
        return energyAmount1;
    }

    public ResourceLocation getEnergyId2() {
        return energyId2;
    }

    public long getEnergyAmount2() {
        return energyAmount2;
    }
}
