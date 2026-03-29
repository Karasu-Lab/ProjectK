package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.ProjectKBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.karasu256.projectk.registry.BlockEntitiesRegistry.blockEntity;

public class ProjectKBlockEntities {
    public static final RegistrySupplier<BlockEntityType<AbyssGeneratorBlockEntity>> ABYSS_GENERATOR =
            blockEntity("abyss_generator", () -> BlockEntityType.Builder.of(AbyssGeneratorBlockEntity::new, ProjectKBlocks.ABYSS_GENERATOR.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<AbyssCoreBlockEntity>> ABYSS_CORE =
            blockEntity("abyss_core", () -> BlockEntityType.Builder.of(AbyssCoreBlockEntity::new, ProjectKBlocks.ABYSS_CORE.get()).build(null));

    public static void init() {
    }
}
