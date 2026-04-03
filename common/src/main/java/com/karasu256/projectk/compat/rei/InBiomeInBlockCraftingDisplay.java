package com.karasu256.projectk.compat.rei;

import com.karasu256.projectk.recipe.BiomeCondition;
import com.karasu256.projectk.recipe.InBiomeInBlockCraftingRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class InBiomeInBlockCraftingDisplay implements Display {
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final ResourceLocation blockTagId;
    private final BiomeCondition biome;

    public InBiomeInBlockCraftingDisplay(InBiomeInBlockCraftingRecipe recipe) {
        this.inputs = recipe.inputs().stream().map(InBiomeInBlockCraftingDisplay::toEntry).toList();
        this.outputs = List.of(EntryIngredients.ofItemStacks(List.of(recipe.result().copy())));
        this.blockTagId = recipe.blockTag();
        this.biome = recipe.biome();
    }

    public static CategoryIdentifier<InBiomeInBlockCraftingDisplay> categoryIdFor(BiomeCondition biome) {
        ResourceLocation id = biome.id();
        String basePath = "in_biome_in_block_crafting/";
        String suffix = biome.tag()
                ? "tag/" + id.getNamespace() + "/" + id.getPath()
                : id.getNamespace() + "/" + id.getPath();
        return CategoryIdentifier.of("projectk", basePath + suffix);
    }

    public static Component biomeName(BiomeCondition biome) {
        ResourceLocation id = biome.id();
        String key;
        if (biome.tag()) {
            key = "tag.worldgen.biome." + id.getNamespace() + "." + id.getPath();
        } else {
            key = "biome." + id.getNamespace() + "." + id.getPath();
        }
        return Component.translatable(key);
    }

    private static EntryIngredient toEntry(IngredientStack requirement) {
        ItemStack[] items = requirement.ingredient().getItems();
        if (items.length == 0) return EntryIngredient.empty();
        List<ItemStack> stacks = Arrays.stream(items).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(requirement.count());
            return copy;
        }).toList();
        if (stacks.isEmpty()) return EntryIngredient.empty();
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
        return categoryIdFor(biome);
    }

    public ResourceLocation getBlockTagId() {
        return blockTagId;
    }

    public Component getRequirementText() {
        Component biomeName = biomeName(biome);
        if (biome.tag()) {
            return Component.translatable("rei.category.projectk.in_biome_in_block_crafting.requirement_tag", biomeName);
        }
        return Component.translatable("rei.category.projectk.in_biome_in_block_crafting.requirement", biomeName);
    }

    public ResourceLocation getBiomeId() {
        return biome.id();
    }

    public ResourceLocation getBiomeIconId() {
        ResourceLocation biomeId = getBiomeId();
        String biomePath = biomeId.getPath();
        String iconPath = "textures/recipes/in_biome_in_block_crafting/" + biomePath + ".png";
        return ResourceLocation.fromNamespaceAndPath(biomeId.getNamespace(), iconPath);
    }
}
