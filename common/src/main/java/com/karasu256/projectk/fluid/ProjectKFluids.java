package com.karasu256.projectk.fluid;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.api.energy.PKMaterials;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.client.util.PKColorUtils;
import com.karasu256.projectk.energy.ProjectKEnergies;
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
                PKMaterials material = findMaterial(definition);
                map.put(definition.id(),
                        registerFluidSet(id, flowingId, material, () -> ProjectKBlocks.getFluidBlock(definition.id()),
                                () -> ProjectKItems.getBucket(definition.id())));
            });

    public static final ArchitecturyFluidAttributes ABYSS_ENERGY_ATTRIBUTES = getAttributes(
            ProjectKEnergies.ABYSS.id());
    public static final RegistrySupplier<FlowingFluid> ABYSS_ENERGY = getSource(ProjectKEnergies.ABYSS.id());
    public static final RegistrySupplier<FlowingFluid> FLOWING_ABYSS_ENERGY = getFlowing(ProjectKEnergies.ABYSS.id());

    public static final ArchitecturyFluidAttributes YIN_ABYSS_ENERGY_ATTRIBUTES = getAttributes(
            ProjectKEnergies.YIN.id());
    public static final RegistrySupplier<FlowingFluid> YIN_ABYSS_ENERGY = getSource(ProjectKEnergies.YIN.id());
    public static final RegistrySupplier<FlowingFluid> FLOWING_YIN_ABYSS_ENERGY = getFlowing(ProjectKEnergies.YIN.id());

    public static final ArchitecturyFluidAttributes YANG_ABYSS_ENERGY_ATTRIBUTES = getAttributes(
            ProjectKEnergies.YANG.id());
    public static final RegistrySupplier<FlowingFluid> YANG_ABYSS_ENERGY = getSource(ProjectKEnergies.YANG.id());
    public static final RegistrySupplier<FlowingFluid> FLOWING_YANG_ABYSS_ENERGY = getFlowing(
            ProjectKEnergies.YANG.id());

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

    public static FluidSet getByMaterial(PKMaterials material) {
        return ABYSS_FLUIDS.get(ProjectKEnergies.getByMaterial(material).id());
    }

    private static FluidSet registerFluidSet(String sourceId, String flowingId, PKMaterials material, Supplier<RegistrySupplier<? extends LiquidBlock>> blockSupplier, Supplier<RegistrySupplier<Item>> bucketSupplier) {
        AtomicReference<RegistrySupplier<FlowingFluid>> sourceRef = new AtomicReference<>();
        AtomicReference<RegistrySupplier<FlowingFluid>> flowingRef = new AtomicReference<>();

        Supplier<Fluid> sourceFluid = () -> sourceRef.get().get();
        Supplier<Fluid> flowingFluid = () -> flowingRef.get().get();

        ResourceLocation stillTexture = Id.id("block/base_fluid_still");
        ResourceLocation flowTexture = Id.id("block/base_fluid_flow");
        int color = material != null ? PKColorUtils.getEnergyColor(Id.id(material.energyIdPath()),
                PKColorUtils.SEMI_TRANSPARENT) : 0x80FFFFFF;
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

    private static PKMaterials findMaterial(ProjectKEnergies.EnergyDefinition definition) {
        for (PKMaterials material : PKMaterials.values()) {
            if (material.energyIdPath().equals(definition.idPath())) {
                return material;
            }
        }
        return null;
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
