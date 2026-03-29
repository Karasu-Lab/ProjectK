package com.karasu256.projectk.registry;

import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class BlockEntitiesRegistry {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static void register() {
        BLOCK_ENTITIES.register();
    }

    public static <T extends net.minecraft.world.level.block.entity.BlockEntity> RegistrySupplier<BlockEntityType<T>> blockEntity(String id, Supplier<BlockEntityType<T>> supplier) {
        return BLOCK_ENTITIES.register(Id.id(id), supplier);
    }
}
