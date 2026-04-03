package com.karasu256.projectk.block;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.custom.*;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.fluid.ProjectKFluids;
import com.karasu256.projectk.platform.PlatformServices;
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

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 1)
public class ProjectKBlocks implements IKRegistryInitializerTarget {
    public static final Map<ResourceLocation, RegistrySupplier<Block>> ABYSS_CORES = EnergyAutoRegistry.mapByEnergy(
            definition -> definition.idPath().replace("_energy", "_core"),
            (definition, id, map) -> map.put(definition.id(), block(id,
                    () -> new AbyssCore(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().sound(SoundType.STONE)),
                    new Item.Properties()
            ))
    );
    public static final Map<ResourceLocation, RegistrySupplier<LiquidBlock>> FLUID_BLOCKS = EnergyAutoRegistry.mapByEnergy(
            definition -> "fluid_" + definition.idPath(),
            (definition, id, map) -> map.put(definition.id(), block(
                    id,
                    () -> PlatformServices.platform().createFluidBlock(ProjectKFluids.getSource(definition.id()), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable())
            ))
    );
    public static final RegistrySupplier<Block> ABYSS_CORE = getCore(ProjectKEnergies.ABYSS.id());
    public static final RegistrySupplier<LiquidBlock> FLUID_ABYSS_ENERGY = getFluidBlock(ProjectKEnergies.ABYSS.id());
    public static final RegistrySupplier<LiquidBlock> FLUID_YIN_ABYSS_ENERGY = getFluidBlock(ProjectKEnergies.YIN.id());
    public static final RegistrySupplier<LiquidBlock> FLUID_YANG_ABYSS_ENERGY = getFluidBlock(ProjectKEnergies.YANG.id());
    public static RegistrySupplier<Block> ABYSS_GENERATOR = block("abyss_generator", () -> new AbyssGenerator(10000L), new Item.Properties());
    public static RegistrySupplier<Block> ABYSS_MAGIC_TABLE = block("abyss_magic_table", () -> new AbyssMagicTable(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE), ProjectKBlock.CustomProperties.of().capacity(10000L)), new Item.Properties());
    public static RegistrySupplier<Block> ABYSS_ALCHEMY_BLEND_MACHINE = block("abyss_alchemy_blend_machine", () -> new AbyssAlchemyBlendMachine(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE), ProjectKBlock.CustomProperties.of().capacity(10000L)), new Item.Properties());
    public static RegistrySupplier<Block> ABYSS_ENERGY_CABLE = block("abyss_energy_cable", () -> new AbyssEnergyCable(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(), ProjectKBlock.CustomProperties.of().capacity(10000L).transferRate(1000L)), new Item.Properties());
    public static RegistrySupplier<Block> KARASIUM_ORE = block("karasium_ore", ProjectKBlock::new, new Item.Properties());
    public static RegistrySupplier<Block> DEEPSLATE_KARASIUM_ORE = block("deepslate_karasium_ore", () -> new ProjectKBlock(Block.Properties.ofFullCopy(Blocks.DEEPSLATE)), new Item.Properties());

    public static void init() {
    }

    public static RegistrySupplier<Block> getCore(ResourceLocation energyId) {
        return ABYSS_CORES.get(energyId);
    }

    public static RegistrySupplier<LiquidBlock> getFluidBlock(ResourceLocation energyId) {
        return FLUID_BLOCKS.get(energyId);
    }
}
