package com.karasu256.projectk.fluid;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.util.PKColorUtils;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.EnergyAutoRegistry;
import com.karasu256.projectk.registry.FluidsRegistry;
import com.karasu256.projectk.utils.Id;
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 0)
public class ProjectKFluids implements IKRegistryInitializerTarget {
    private static final Map<ResourceLocation, FluidSet> ABYSS_FLUIDS = EnergyAutoRegistry.mapByEnergy(
            definition -> "fluid_" + definition.idPath(), (definition, id, map) -> {
                String flowingId = "flowing_" + id;
                map.put(definition.id(),
                        registerFluidSet(id, flowingId, definition.id(),
                                () -> ProjectKBlocks.getFluidBlock(definition.id()),
                                () -> ProjectKItems.getBucket(definition.id())));
            });


    public static void init() {
    }

    public static RegistrySupplier<FlowingFluid> getSource(ResourceLocation energyId) {
        return ABYSS_FLUIDS.get(energyId).source();
    }

    public static RegistrySupplier<FlowingFluid> getFlowing(ResourceLocation energyId) {
        return ABYSS_FLUIDS.get(energyId).flowing();
    }

    public static ArchitecturyFluidAttributes getAttributes(ResourceLocation energyId) {
        return ABYSS_FLUIDS.get(energyId).attributes();
    }


    private static FluidSet registerFluidSet(String sourceId, String flowingId, ResourceLocation energyId, Supplier<RegistrySupplier<? extends LiquidBlock>> blockSupplier, Supplier<RegistrySupplier<Item>> bucketSupplier) {
        AtomicReference<RegistrySupplier<FlowingFluid>> sourceRef = new AtomicReference<>();
        AtomicReference<RegistrySupplier<FlowingFluid>> flowingRef = new AtomicReference<>();

        Supplier<Fluid> sourceFluid = () -> sourceRef.get().get();
        Supplier<Fluid> flowingFluid = () -> flowingRef.get().get();

        ResourceLocation stillTexture = Id.id("block/base_fluid_still");
        ResourceLocation flowTexture = Id.id("block/base_fluid_flow");
        int color = PKColorUtils.getEnergyColor(energyId, PKColorUtils.SEMI_TRANSPARENT);
        SimpleArchitecturyFluidAttributes attributes = new ProjectKFluidAttributes(sourceFluid,
                flowingFluid).sourceTexture(stillTexture).flowingTexture(flowTexture).color(color)
                .blockSupplier(blockSupplier);

        if (bucketSupplier != null) {
            attributes.bucketItemSupplier(bucketSupplier);
        }

        Supplier<FlowingFluid> sourceSupplier = () -> sourceRef.get().get();
        Supplier<FlowingFluid> flowingSupplier = () -> flowingRef.get().get();

        sourceRef.set(FluidsRegistry.fluid(sourceId,
                () -> new ProjectKFlowingFluid.Source(attributes, sourceSupplier, flowingSupplier)));
        flowingRef.set(FluidsRegistry.fluid(flowingId,
                () -> new ProjectKFlowingFluid.Flowing(attributes, sourceSupplier, flowingSupplier)));

        return new FluidSet(attributes, sourceRef.get(), flowingRef.get());
    }

    private static ResourceLocation fluidTexture(String baseName, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + baseName + "_" + suffix);
    }


    public record FluidSet(ArchitecturyFluidAttributes attributes, RegistrySupplier<FlowingFluid> source,
                           RegistrySupplier<FlowingFluid> flowing) {
    }

    private static final class ProjectKFluidAttributes extends SimpleArchitecturyFluidAttributes {
        private ProjectKFluidAttributes(Supplier<? extends Fluid> source, Supplier<? extends Fluid> flowing) {
            super(source, flowing);
        }
    }
}
