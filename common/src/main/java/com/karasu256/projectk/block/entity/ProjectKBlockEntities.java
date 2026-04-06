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
    private static Block[] coreBlocks() {
        Collection<RegistrySupplier<Block>> cores = ProjectKBlocks.ABYSS_CORES.values();
        Block[] blocks = new Block[cores.size()];
        int index = 0;
        for (RegistrySupplier<Block> core : cores) {
            blocks[index++] = core.get();
        }
        return blocks;
    }

    public static void init() {
    }

    public static final RegistrySupplier<BlockEntityType<AbyssGeneratorBlockEntity>> ABYSS_GENERATOR = blockEntity(
            "abyss_generator",
            () -> BlockEntityType.Builder.of(AbyssGeneratorBlockEntity::new, ProjectKBlocks.ABYSS_GENERATOR.get())
                    .build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssAbsorptionPrismBlockEntity>> ABYSS_ABSORPTION_PRISM = blockEntity(
            "abyss_absorption_prism",
            () -> BlockEntityType.Builder.of(AbyssAbsorptionPrismBlockEntity::new,
                            ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get())
                    .build(null));


    public static final RegistrySupplier<BlockEntityType<AbyssCoreBlockEntity>> ABYSS_CORE = blockEntity("abyss_core",
            () -> BlockEntityType.Builder.of(AbyssCoreBlockEntity::new, coreBlocks()).build(null));

    public static final RegistrySupplier<BlockEntityType<AbyssMagicTableBlockEntity>> ABYSS_MAGIC_TABLE = blockEntity(
            "abyss_magic_table",
            () -> BlockEntityType.Builder.of(AbyssMagicTableBlockEntity::new, ProjectKBlocks.ABYSS_MAGIC_TABLE.get())
                    .build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssAlchemyBlendMachineBlockEntity>> ABYSS_ALCHEMY_BLEND_MACHINE = blockEntity(
            "abyss_alchemy_blend_machine", () -> BlockEntityType.Builder.of(AbyssAlchemyBlendMachineBlockEntity::new,
                    ProjectKBlocks.ABYSS_ALCHEMY_BLEND_MACHINE.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssEnchanterBlockEntity>> ABYSS_ENCHANTER = blockEntity(
            "abyss_enchanter",
            () -> BlockEntityType.Builder.of(AbyssEnchanterBlockEntity::new, ProjectKBlocks.ABYSS_ENCHANTER.get())
                    .build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssChargerBlockEntity>> ABYSS_CHARGER = blockEntity(
            "abyss_charger",
            () -> BlockEntityType.Builder.of(AbyssChargerBlockEntity::new, ProjectKBlocks.ABYSS_CHARGER.get())
                    .build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssStorageBlockEntity>> ABYSS_STORAGE = blockEntity(
            "abyss_storage",
            () -> BlockEntityType.Builder.of(AbyssStorageBlockEntity::new, ProjectKBlocks.ABYSS_STORAGE.get())
                    .build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssEnchantRemoverBlockEntity>> ABYSS_ENCHANT_REMOVER = blockEntity(
            "abyss_enchant_remover", () -> BlockEntityType.Builder.of(AbyssEnchantRemoverBlockEntity::new,
                    ProjectKBlocks.ABYSS_ENCHANT_REMOVER.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<AbyssEnergyCableBlockEntity>> ABYSS_ENERGY_CABLE = blockEntity(
            "abyss_energy_cable",
            () -> BlockEntityType.Builder.of(AbyssEnergyCableBlockEntity::new, ProjectKBlocks.ABYSS_ENERGY_CABLE.get())
                    .build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssSynthesizerBlockEntity>> ABYSS_SYNTHESIZER = blockEntity(
            "abyss_synthesizer",
            () -> BlockEntityType.Builder.of(AbyssSynthesizerBlockEntity::new, ProjectKBlocks.ABYSS_SYNTHESIZER.get())
                    .build(null));
    public static final RegistrySupplier<BlockEntityType<AbyssLaserEmitterBlockEntity>> ABYSS_LASER_EMITTER = blockEntity(
            "abyss_laser_emitter",
            () -> BlockEntityType.Builder.of(AbyssLaserEmitterBlockEntity::new,
                            ProjectKBlocks.ABYSS_LASER_EMITTER.get())
                    .build(null));


}
