package com.karasu256.projectk.block;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.custom.*;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.item.custom.AbyssCoreItem;
import com.karasu256.projectk.item.custom.AbyssMachineBlockItem;
import com.karasu256.projectk.platform.PlatformServices;
import com.karasu256.projectk.registry.BlocksRegistry;
import com.karasu256.projectk.registry.CreativeTabsRegistry;
import com.karasu256.projectk.registry.EnergyAutoRegistry;
import com.karasu256.projectk.registry.ItemsRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.karasu256.projectk.registry.CreativeTabsRegistry.BUILDING_BLOCKS;
import static com.karasu256.projectk.registry.CreativeTabsRegistry.MACHINES;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 1)
public class ProjectKBlocks implements IKRegistryInitializerTarget {
    public static final Map<ResourceLocation, RegistrySupplier<LiquidBlock>> FLUID_BLOCKS = EnergyAutoRegistry.mapByEnergy(
            definition -> "fluid_" + definition.idPath(), (definition, id, map) -> map.put(definition.id(),
                    BlocksRegistry.block(id, () -> PlatformServices.platform()
                            .createFluidBlock(ProjectKFluids.getSource(definition.id()),
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()))));

    private static final Map<RegistrySupplier<? extends Block>, Map<BlockMaterials, RegistrySupplier<? extends Block>>> BLOCK_SET_MAP = new HashMap<>();
    private static final List<RegistrySupplier<? extends Block>> ALL_BLOCKS = new ArrayList<>();
    private static final Map<BlockMaterials, List<RegistrySupplier<? extends Block>>> BLOCKS_BY_MATERIAL = new EnumMap<>(
            BlockMaterials.class);
    private static final List<BlockItemInfo> BLOCK_ITEM_INFOS = new ArrayList<>();

    public record BlockItemInfo(RegistrySupplier<? extends Block> block, Item.Properties properties,
                                CreativeTabsRegistry.TabCategory category) {
    }

    private static final Map<RegistrySupplier<? extends Block>, ModelInfo> BLOCK_MODEL_INFOS = new LinkedHashMap<>();

    public sealed interface ModelInfo {
        record Simple() implements ModelInfo {
        }

        record Existing(String path) implements ModelInfo {
        }

        record CubeBottomTop(String base, String side, String bottom, String top) implements ModelInfo {
        }

        record Custom() implements ModelInfo {
        }
    }

    static {
        for (BlockMaterials material : BlockMaterials.values()) {
            BLOCKS_BY_MATERIAL.put(material, new ArrayList<>());
        }
    }

    public static final RegistrySupplier<Block> ABYSS_CORE = registerBlock("abyss_core", () -> new AbyssCore(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.STONE)),
            b -> new AbyssCoreItem(b, new Item.Properties()), true, new ModelInfo.Existing("projectk:block/abyss_core"),
            MACHINES);

    public static final RegistrySupplier<Block> ABYSS_GENERATOR = registerBlock("abyss_generator",
            () -> new AbyssGenerator(10000L), b -> new AbyssMachineBlockItem(b, new Item.Properties(), 0L), false,
            new ModelInfo.CubeBottomTop("abyss_generator", "abyss_energy/side", "abyss_energy/bottom",
                    "abyss_energy/top"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_MAGIC_TABLE = registerBlock("abyss_magic_table",
            () -> new AbyssMagicTable(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE),
                    ProjectKBlock.CustomProperties.of().capacity(10000L)), new Item.Properties(),
            new ModelInfo.Existing("projectk:block/abyss_magic_table"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ALCHEMY_BLEND_MACHINE = registerBlock(
            "abyss_alchemy_blend_machine",
            () -> new AbyssAlchemyBlendMachine(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE),
                    ProjectKBlock.CustomProperties.of().capacity(10000L)), new Item.Properties(),
            new ModelInfo.Existing("projectk:block/abyss_alchemy_blend_machine"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ENCHANTER = registerBlock("abyss_enchanter",
            () -> new AbyssEnchanter(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE),
                    ProjectKBlock.CustomProperties.of().capacity(30000L)), new Item.Properties(),
            new ModelInfo.Existing("projectk:block/abyss_enchanter"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_CHARGER = registerBlock("abyss_charger",
            () -> new AbyssCharger(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    ProjectKBlock.CustomProperties.of().capacity(30000L).transferRate(1000L)), new Item.Properties(),
            new ModelInfo.Existing("projectk:block/abyss_charger"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_STORAGE = registerBlock("abyss_storage",
            () -> new AbyssStorage(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    AbyssStorage.Properties.of().capacity(30000L).maxTypes(3)),
            b -> new AbyssMachineBlockItem(b, new Item.Properties(), 0L), false,
            new ModelInfo.Existing("projectk:block/abyss_storage"), MACHINES);

    public static final RegistrySupplier<Block> CREATIVE_ABYSS_STORAGE = registerBlock("creative_abyss_storage",
            () -> new CreativeAbyssStorage(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    AbyssStorage.Properties.of().capacity(0L).maxTypes(1)),
            b -> new AbyssMachineBlockItem(b, new Item.Properties(), Long.MAX_VALUE / 2), false,
            new ModelInfo.Existing("projectk:block/abyss_storage"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ENCHANT_REMOVER = registerBlock("abyss_enchant_remover",
            () -> new AbyssEnchantRemover(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    AbyssEnchantRemover.Properties.of().defaultBookCapacity(30000L)), new Item.Properties(),
            new ModelInfo.Existing("projectk:block/abyss_enchant_remover"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ENERGY_CABLE = registerBlock("abyss_energy_cable",
            () -> new AbyssEnergyCable(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.STONE),
                    ProjectKBlock.CustomProperties.of().capacity(10000L).transferRate(1000L)), new Item.Properties(),
            new ModelInfo.Custom(), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_SYNTHESIZER = registerBlock("abyss_synthesizer",
            () -> new AbyssSynthesizer(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    ProjectKBlock.CustomProperties.of().capacity(100000L)), new Item.Properties(),
            new ModelInfo.Existing("projectk:block/abyss_synthesizer"), MACHINES);

    public static final RegistrySupplier<Block> ABYSS_LASER_EMITTER = registerBlock("abyss_laser_emitter",
            () -> new AbyssLaserEmitter(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    new AbstractEnergyBlock.Properties(30000L)), new Item.Properties(), new ModelInfo.Custom(),
            MACHINES);

    public static final RegistrySupplier<Block> ABYSS_ABSORPTION_PRISM = registerBlock("abyss_absorption_prism",
            () -> new ProjectKBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion().sound(SoundType.GLASS)),
            new Item.Properties(), new ModelInfo.Simple(), MACHINES);

    public static final RegistrySupplier<Block> KARASIUM_ORE = registerSimpleBlock("karasium_ore", ProjectKBlock::new,
            new Item.Properties(), BUILDING_BLOCKS);

    public static final RegistrySupplier<Block> DEEPSLATE_KARASIUM_ORE = registerSimpleBlock("deepslate_karasium_ore",
            () -> new ProjectKBlock(Block.Properties.ofFullCopy(Blocks.DEEPSLATE)), new Item.Properties(),
            BUILDING_BLOCKS);

    public static final RegistrySupplier<Block> ABYSS_PORTAL = registerBlock("abyss_portal", () -> new AbyssPortal(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.STONE)),
            new Item.Properties(), new ModelInfo.Custom(), BUILDING_BLOCKS);

    public static final RegistrySupplier<Block> POLISHED_NETHERRACK = registerBlockSet("polished_netherrack",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERRACK)), new Item.Properties(),
            BUILDING_BLOCKS);

    public static void init() {
    }

    public static <T extends Block> RegistrySupplier<T> registerBlock(String id, Supplier<T> block, Item.Properties itemProperties, CreativeTabsRegistry.TabCategory category) {
        return registerBlock(id, block, itemProperties, false, new ModelInfo.Simple(), category);
    }

    public static <T extends Block> RegistrySupplier<T> registerBlock(String id, Supplier<T> block, Item.Properties itemProperties, ModelInfo modelInfo, CreativeTabsRegistry.TabCategory category) {
        return registerBlock(id, block, itemProperties, false, modelInfo, category);
    }

    public static <T extends Block> RegistrySupplier<T> registerBlock(String id, Supplier<T> block, Item.Properties itemProperties, boolean energySuffix, ModelInfo modelInfo, CreativeTabsRegistry.TabCategory category) {
        RegistrySupplier<T> registered = BlocksRegistry.block(id, block);
        ALL_BLOCKS.add(registered);
        BLOCK_MODEL_INFOS.put(registered, modelInfo);
        BLOCK_ITEM_INFOS.add(new BlockItemInfo(registered, itemProperties, category));
        return registered;
    }

    public static <T extends Block, I extends Item> RegistrySupplier<T> registerBlock(String id, Supplier<T> block, Function<T, I> itemFactory, boolean energySuffix, ModelInfo modelInfo, CreativeTabsRegistry.TabCategory category) {
        RegistrySupplier<T> registered = BlocksRegistry.block(id, block);
        ALL_BLOCKS.add(registered);
        BLOCK_MODEL_INFOS.put(registered, modelInfo);
        ItemsRegistry.item(id, () -> itemFactory.apply(registered.get()), energySuffix, category);
        return registered;
    }

    public static <T extends Block> RegistrySupplier<T> registerSimpleBlock(String id, Supplier<T> block, Item.Properties itemProperties, CreativeTabsRegistry.TabCategory category) {
        RegistrySupplier<T> registered = registerBlock(id, block, itemProperties, category);
        BLOCKS_BY_MATERIAL.get(BlockMaterials.FULL).add(registered);
        return registered;
    }

    public static RegistrySupplier<Block> registerBlockSet(String name, Supplier<Block> fullBlockSupplier, Item.Properties itemProperties, CreativeTabsRegistry.TabCategory category) {
        RegistrySupplier<Block> full = registerBlock(name, fullBlockSupplier, itemProperties, new ModelInfo.Simple(),
                category);
        BLOCKS_BY_MATERIAL.get(BlockMaterials.FULL).add(full);
        RegistrySupplier<StairBlock> stairs = BlocksRegistry.block(name + "_stairs",
                () -> new StairBlock(full.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(full.get())));
        RegistrySupplier<SlabBlock> slab = BlocksRegistry.block(name + "_slab",
                () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(full.get())));

        Map<BlockMaterials, RegistrySupplier<? extends Block>> set = new EnumMap<>(BlockMaterials.class);
        set.put(BlockMaterials.FULL, full);
        set.put(BlockMaterials.STAIR, stairs);
        set.put(BlockMaterials.HALF, slab);

        BLOCK_SET_MAP.put(full, set);

        ALL_BLOCKS.add(stairs);
        BLOCKS_BY_MATERIAL.get(BlockMaterials.STAIR).add(stairs);
        BLOCK_ITEM_INFOS.add(new BlockItemInfo(stairs, itemProperties, category));

        ALL_BLOCKS.add(slab);
        BLOCKS_BY_MATERIAL.get(BlockMaterials.HALF).add(slab);
        BLOCK_ITEM_INFOS.add(new BlockItemInfo(slab, itemProperties, category));

        return full;
    }

    public static Map<RegistrySupplier<? extends Block>, ModelInfo> getBlockModelInfos() {
        return BLOCK_MODEL_INFOS;
    }

    public static RegistrySupplier<? extends Block> getByMaterial(RegistrySupplier<? extends Block> baseBlock, BlockMaterials material) {
        Map<BlockMaterials, RegistrySupplier<? extends Block>> set = BLOCK_SET_MAP.get(baseBlock);
        return set != null ? set.get(material) : null;
    }

    public static Map<RegistrySupplier<? extends Block>, Map<BlockMaterials, RegistrySupplier<? extends Block>>> getBlockSets() {
        return BLOCK_SET_MAP;
    }

    public static List<RegistrySupplier<? extends Block>> getBlocksByMaterial(BlockMaterials material) {
        return BLOCKS_BY_MATERIAL.get(material);
    }

    public static List<BlockItemInfo> getBlockItemInfos() {
        return BLOCK_ITEM_INFOS;
    }

    public static List<RegistrySupplier<? extends Block>> getBlocks() {
        return ALL_BLOCKS;
    }

    public static RegistrySupplier<Block> getCore(ResourceLocation energyId) {
        return ABYSS_CORE;
    }

    public static RegistrySupplier<LiquidBlock> getFluidBlock(ResourceLocation energyId) {
        return FLUID_BLOCKS.get(energyId);
    }
}
