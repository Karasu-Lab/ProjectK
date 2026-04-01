package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 8)
public class RecipesRegistry implements IKRegistryTarget {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ProjectK.MOD_ID, Registries.RECIPE_SERIALIZER);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ProjectK.MOD_ID, Registries.RECIPE_TYPE);

    public static void register() {
        SERIALIZERS.register();
        TYPES.register();
    }

    public static <T extends RecipeSerializer<?>> RegistrySupplier<T> serializer(String id, Supplier<T> serializer) {
        return SERIALIZERS.register(id, serializer);
    }

    public static <T extends RecipeType<?>> RegistrySupplier<T> type(String id, Supplier<T> type) {
        return TYPES.register(id, type);
    }
}
