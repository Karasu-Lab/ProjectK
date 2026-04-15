package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.api.datagen.impl.AbstractInBiomeInBlockCraftingProvider;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.recipe.BiomeCondition;
import com.karasu256.projectk.recipe.InBiomeInBlockCraftingRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import com.karasu256.projectk.registry.ProjectKTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InBiomeInBlockCraftingProvider extends AbstractInBiomeInBlockCraftingProvider {
    public InBiomeInBlockCraftingProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, "in_biome_in_block", registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        super.buildRecipes(output);
        addWitherRoseRecipe();
    }

    private void addWitherRoseRecipe() {
        ItemStack result = new ItemStack(Items.WITHER_ROSE);
        result.set(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);

        InBiomeInBlockCraftingRecipe recipe = new InBiomeInBlockCraftingRecipe(
                new BiomeCondition(Biomes.SOUL_SAND_VALLEY.location(), false),
                ProjectKTags.Blocks.IN_BIOME_IN_BLOCK_CRAFTING.location(),
                List.of(new IngredientStack(Ingredient.of(Items.POPPY), 1),
                        new IngredientStack(Ingredient.of(ProjectKItems.WITHER_BONE.get()), 1)), 1.0f, result);

        addInBiomeInBlockCrafting(recipe);
    }
}
