package com.karasu256.projectk.block;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.custom.*;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.item.custom.AbyssCoreItem;
import com.karasu256.projectk.platform.PlatformServices;
import com.karasu256.projectk.registry.BlocksRegistry;
import com.karasu256.projectk.registry.EnergyAutoRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Map;

import static com.karasu256.projectk.registry.BlocksRegistry.block;
import static com.karasu256.projectk.registry.CreativeTabsRegistry.*;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 1)
public class ProjectKBlocks implements IKRegistryInitializerTarget {
    public static final Map<ResourceLocation, RegistrySupplier<LiquidBlock>> FLUID_BLOCKS = EnergyAutoRegistry.mapByEnergy(
            definition -> "fluid_" + definition.idPath(), (definition, id, map) -> map.put(definition.id(), BlocksRegistry.block(id,
                    () -> PlatformServices.platform().createFluidBlock(ProjectKFluids.getSource(definition.id()),
                            BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()))));

    public static final RegistrySupplier<Block> ABYSS_CORE = block("abyss_core", () -> new AbyssCore(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.STONE)),
            b -> new AbyssCoreItem(b, new Item.Properties()), true, MATERIALS);

    public static final RegistrySupplier<Block> ABYSS_GENERATOR = block("abyss_generator", () -> new AbyssGenerator(10000L),
            new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_MAGIC_TABLE = block("abyss_magic_table",
            () -> new AbyssMagicTable(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE),
                    ProjectKBlock.CustomProperties.of().capacity(10000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ALCHEMY_BLEND_MACHINE = block("abyss_alchemy_blend_machine",
            () -> new AbyssAlchemyBlendMachine(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE),
                    ProjectKBlock.CustomProperties.of().capacity(10000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ENCHANTER = block("abyss_enchanter",
            () -> new AbyssEnchanter(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE),
                    ProjectKBlock.CustomProperties.of().capacity(30000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_CHARGER = block("abyss_charger",
            () -> new AbyssCharger(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    ProjectKBlock.CustomProperties.of().capacity(30000L).transferRate(1000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_STORAGE = block("abyss_storage",
            () -> new AbyssStorage(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    AbyssStorage.Properties.of().capacity(30000L).maxTypes(3)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> CREATIVE_ABYSS_STORAGE = block("creative_abyss_storage",
            () -> new CreativeAbyssStorage(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    AbyssStorage.Properties.of().capacity(0L).maxTypes(1)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ENCHANT_REMOVER = block("abyss_enchant_remover",
            () -> new AbyssEnchantRemover(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    AbyssEnchantRemover.Properties.of().defaultBookCapacity(30000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ENERGY_CABLE = block("abyss_energy_cable",
            () -> new AbyssEnergyCable(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(),
                    ProjectKBlock.CustomProperties.of().capacity(10000L).transferRate(1000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_SYNTHESIZER = block("abyss_synthesizer",
            () -> new AbyssSynthesizer(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    ProjectKBlock.CustomProperties.of().capacity(100000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_LASER_EMITTER = block("abyss_laser_emitter",
            () -> new AbyssLaserEmitter(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    new AbstractEnergyBlock.Properties(30000L)), new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ABSORPTION_PRISM = block("abyss_absorption_prism",
            () -> new AbyssAbsorptionPrism(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion().sound(SoundType.GLASS)),
            new Item.Properties(), MACHINES);

    public static final RegistrySupplier<Block> KARASIUM_ORE = block("karasium_ore", ProjectKBlock::new,
            new Item.Properties(), BUILDING_BLOCKS);

    public static final RegistrySupplier<Block> DEEPSLATE_KARASIUM_ORE = block("deepslate_karasium_ore",
            () -> new ProjectKBlock(Block.Properties.ofFullCopy(Blocks.DEEPSLATE)), new Item.Properties(), BUILDING_BLOCKS);

    public static final RegistrySupplier<Block> ABYSS_PORTAL = block("abyss_portal", () -> new AbyssPortal(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.STONE)),
            new Item.Properties(), BUILDING_BLOCKS);

    public static final RegistrySupplier<Block> POLISHED_NETHERRACK = block("polished_netherrack",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERRACK)),
            new Item.Properties(), BUILDING_BLOCKS);

    public static void init() {
    }

    public static RegistrySupplier<Block> getCore(ResourceLocation energyId) {
        return ABYSS_CORE;
    }

    public static RegistrySupplier<LiquidBlock> getFluidBlock(ResourceLocation energyId) {
        return FLUID_BLOCKS.get(energyId);
    }
}
