package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.recipe.AbyssAlchemyBlendRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class AbyssAlchemyBlendRecipeProvider extends RecipeProvider {
    private RecipeOutput output;

    public AbyssAlchemyBlendRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        this.output = output;

        ItemStack result = new ItemStack(ProjectKItems.TIER_UPGRADE.get());
        AbyssEnergyData.applyToStack(result, ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL),
                2000L);
        addBlendRecipe(new AbyssAlchemyBlendRecipe(
                ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.YIN), 1000L,
                ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.YANG), 1000L,
                new IngredientStack(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 1),
                result
        ));
    }

    @Override
    public String getName() {
        return "ProjectK Abyss Alchemy Blend Recipes";
    }

    private void addBlendRecipe(AbyssAlchemyBlendRecipe recipe) {
        String resultPath = BuiltInRegistries.ITEM.getKey(recipe.result().getItem()).getPath();
        output.accept(Id.id(resultPath + "_from_alchemy_blend"), recipe, null);
    }
}
