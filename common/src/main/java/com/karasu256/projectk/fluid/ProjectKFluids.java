package com.karasu256.projectk.fluid;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.FluidsRegistry;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 0)
public class ProjectKFluids implements IKRegistryInitializerTarget {
    private static final FluidSet ABYSS = registerFluidSet("fluid_abyss_energy", "flowing_fluid_abyss_energy", () -> ProjectKBlocks.FLUID_ABYSS_ENERGY, () -> ProjectKItems.BUCKET_OF_ABYSS_ENERGY);
    public static final ArchitecturyFluidAttributes ABYSS_ENERGY_ATTRIBUTES = ABYSS.attributes();
    public static final RegistrySupplier<FlowingFluid> ABYSS_ENERGY = ABYSS.source();
    public static final RegistrySupplier<FlowingFluid> FLOWING_ABYSS_ENERGY = ABYSS.flowing();

    private static final FluidSet YIN = registerFluidSet("fluid_yin_abyss_energy", "flowing_fluid_yin_abyss_energy", () -> ProjectKBlocks.FLUID_YIN_ABYSS_ENERGY, () -> ProjectKItems.BUCKET_OF_YIN_ABYSS_ENERGY);
    public static final ArchitecturyFluidAttributes YIN_ABYSS_ENERGY_ATTRIBUTES = YIN.attributes();
    public static final RegistrySupplier<FlowingFluid> YIN_ABYSS_ENERGY = YIN.source();
    public static final RegistrySupplier<FlowingFluid> FLOWING_YIN_ABYSS_ENERGY = YIN.flowing();

    private static final FluidSet YANG = registerFluidSet("fluid_yang_abyss_energy", "flowing_fluid_yang_abyss_energy", () -> ProjectKBlocks.FLUID_YANG_ABYSS_ENERGY, () -> ProjectKItems.BUCKET_OF_YANG_ABYSS_ENERGY);
    public static final ArchitecturyFluidAttributes YANG_ABYSS_ENERGY_ATTRIBUTES = YANG.attributes();
    public static final RegistrySupplier<FlowingFluid> YANG_ABYSS_ENERGY = YANG.source();
    public static final RegistrySupplier<FlowingFluid> FLOWING_YANG_ABYSS_ENERGY = YANG.flowing();

    public static void init() {
    }

    private static FluidSet registerFluidSet(String sourceId, String flowingId, Supplier<RegistrySupplier<? extends LiquidBlock>> blockSupplier, Supplier<RegistrySupplier<Item>> bucketSupplier) {
        AtomicReference<RegistrySupplier<FlowingFluid>> sourceRef = new AtomicReference<>();
        AtomicReference<RegistrySupplier<FlowingFluid>> flowingRef = new AtomicReference<>();

        Supplier<Fluid> sourceFluid = () -> sourceRef.get().get();
        Supplier<Fluid> flowingFluid = () -> flowingRef.get().get();

        ResourceLocation stillTexture = fluidTexture(sourceId, "still");
        ResourceLocation flowTexture = fluidTexture(sourceId, "flow");
        SimpleArchitecturyFluidAttributes attributes = new ProjectKFluidAttributes(sourceFluid, flowingFluid)
            .sourceTexture(stillTexture)
            .flowingTexture(flowTexture)
            .blockSupplier(blockSupplier);

        if (bucketSupplier != null) {
            attributes.bucketItemSupplier(bucketSupplier);
        }

        Supplier<FlowingFluid> sourceSupplier = () -> sourceRef.get().get();
        Supplier<FlowingFluid> flowingSupplier = () -> flowingRef.get().get();

        sourceRef.set(FluidsRegistry.fluid(sourceId, () -> new ProjectKFlowingFluid.Source(attributes, sourceSupplier, flowingSupplier)));
        flowingRef.set(FluidsRegistry.fluid(flowingId, () -> new ProjectKFlowingFluid.Flowing(attributes, sourceSupplier, flowingSupplier)));

        return new FluidSet(attributes, sourceRef.get(), flowingRef.get());
    }

    private record FluidSet(ArchitecturyFluidAttributes attributes, RegistrySupplier<FlowingFluid> source,
                            RegistrySupplier<FlowingFluid> flowing) {
    }

    private static ResourceLocation fluidTexture(String baseName, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + baseName + "_" + suffix);
    }

    private static final class ProjectKFluidAttributes extends SimpleArchitecturyFluidAttributes {
        private ProjectKFluidAttributes(Supplier<? extends Fluid> source, Supplier<? extends Fluid> flowing) {
            super(source, flowing);
        }
    }
}
