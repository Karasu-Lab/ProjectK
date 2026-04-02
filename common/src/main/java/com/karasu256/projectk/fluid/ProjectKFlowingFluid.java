package com.karasu256.projectk.fluid;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public final class ProjectKFlowingFluid {
    private ProjectKFlowingFluid() {
    }

    public static class Source extends ArchitecturyFlowingFluid.Source {
        private final Supplier<FlowingFluid> sourceSupplier;
        private final Supplier<FlowingFluid> flowingSupplier;

        public Source(ArchitecturyFluidAttributes attributes, Supplier<FlowingFluid> sourceSupplier, Supplier<FlowingFluid> flowingSupplier) {
            super(attributes);
            this.sourceSupplier = sourceSupplier;
            this.flowingSupplier = flowingSupplier;
        }

        @Override
        public FlowingFluid getFlowing() {
            return flowingSupplier.get();
        }

        @Override
        public FlowingFluid getSource() {
            return sourceSupplier.get();
        }
    }

    public static class Flowing extends ArchitecturyFlowingFluid.Flowing {
        private final Supplier<FlowingFluid> sourceSupplier;
        private final Supplier<FlowingFluid> flowingSupplier;

        public Flowing(ArchitecturyFluidAttributes attributes, Supplier<FlowingFluid> sourceSupplier, Supplier<FlowingFluid> flowingSupplier) {
            super(attributes);
            this.sourceSupplier = sourceSupplier;
            this.flowingSupplier = flowingSupplier;
        }

        @Override
        public FlowingFluid getFlowing() {
            return flowingSupplier.get();
        }

        @Override
        public FlowingFluid getSource() {
            return sourceSupplier.get();
        }
    }
}
