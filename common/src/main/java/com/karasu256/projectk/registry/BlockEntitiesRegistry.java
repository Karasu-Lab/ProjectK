package com.karasu256.projectk.registry;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.KGeneratorBlockEntity;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class BlockEntitiesRegistry {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<KGeneratorBlockEntity>> K_GENERATOR = BLOCK_ENTITIES.register(
            Id.id("k_generator"),
            () -> BlockEntityType.Builder.of(KGeneratorBlockEntity::new, ProjectKBlocks.K_GENERATOR.get()).build(null)
    );

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
