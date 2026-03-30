package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.karasu256.projectk.registry.BlockEntitiesRegistry.blockEntity;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 3)
public class ProjectKBlockEntities implements IKRegistryInitializerTarget {
    public static final RegistrySupplier<BlockEntityType<AbyssGeneratorBlockEntity>> ABYSS_GENERATOR =
            blockEntity("abyss_generator", () -> BlockEntityType.Builder.of(AbyssGeneratorBlockEntity::new, ProjectKBlocks.ABYSS_GENERATOR.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<AbyssCoreBlockEntity>> ABYSS_CORE =
            blockEntity("abyss_core", () -> BlockEntityType.Builder.of(AbyssCoreBlockEntity::new, ProjectKBlocks.ABYSS_CORE.get()).build(null));

    public static void init() {
    }
}
