package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collection;

import static com.karasu256.projectk.registry.BlockEntitiesRegistry.blockEntity;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 3)
public class ProjectKBlockEntities implements IKRegistryInitializerTarget {
    public static void init() {
    }    public static final RegistrySupplier<BlockEntityType<AbyssGeneratorBlockEntity>> ABYSS_GENERATOR = blockEntity("abyss_generator", () -> BlockEntityType.Builder.of(AbyssGeneratorBlockEntity::new, ProjectKBlocks.ABYSS_GENERATOR.get()).build(null));

    private static Block[] coreBlocks() {
        Collection<RegistrySupplier<Block>> cores = ProjectKBlocks.ABYSS_CORES.values();
        Block[] blocks = new Block[cores.size()];
        int index = 0;
        for (RegistrySupplier<Block> core : cores) {
            blocks[index++] = core.get();
        }
        return blocks;
    }    public static final RegistrySupplier<BlockEntityType<AbyssCoreBlockEntity>> ABYSS_CORE = blockEntity("abyss_core", () -> BlockEntityType.Builder.of(AbyssCoreBlockEntity::new, coreBlocks()).build(null));

    public static final RegistrySupplier<BlockEntityType<AbyssMagicTableBlockEntity>> ABYSS_MAGIC_TABLE = blockEntity("abyss_magic_table", () -> BlockEntityType.Builder.of(AbyssMagicTableBlockEntity::new, ProjectKBlocks.ABYSS_MAGIC_TABLE.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<AbyssEnergyCableBlockEntity>> ABYSS_ENERGY_CABLE = blockEntity("abyss_energy_cable", () -> BlockEntityType.Builder.of(AbyssEnergyCableBlockEntity::new, ProjectKBlocks.ABYSS_ENERGY_CABLE.get()).build(null));




}
