package com.karasu256.projectk.recipe;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.registry.RecipesRegistry;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 3)
public class ProjectKRecipes implements IKRegistryInitializerTarget {
    public static final RegistrySupplier<RecipeSerializer<InBiomeInBlockCraftingRecipe>> IN_BIOME_IN_BLOCK_CRAFTING_SERIALIZER = RecipesRegistry.serializer("in_biome_in_block_crafting", InBiomeInBlockCraftingRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<AbyssMagicTableRecipe>> ABYSS_MAGIC_TABLE_SERIALIZER = RecipesRegistry.serializer("abyss_magic_table", AbyssMagicTableRecipe.Serializer::new);

    public static final RegistrySupplier<RecipeType<InBiomeInBlockCraftingRecipe>> IN_BIOME_IN_BLOCK_CRAFTING = RecipesRegistry.type("in_biome_in_block_crafting", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return Id.id("in_biome_in_block_crafting").toString();
        }
    });

    public static final RegistrySupplier<RecipeType<AbyssMagicTableRecipe>> ABYSS_MAGIC_TABLE = RecipesRegistry.type("abyss_magic_table", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return Id.id("abyss_magic_table").toString();
        }
    });

    public static void init() {
    }
}
