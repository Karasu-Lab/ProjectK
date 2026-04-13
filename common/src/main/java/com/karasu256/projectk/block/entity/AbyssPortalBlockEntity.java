package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssPortal;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.particle.IAbyssParticleMoveable;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssPortalBlockEntity extends AbstractAbyssMachineBlockEntity implements IAbyssParticleMoveable {
    public AbyssPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_PORTAL.get(), pos, state, ProjectKMachineCapacities.ABYSS_PORTAL, 6);
        addItemSlot(Id.id("prism_extension"));
    }

    public void tick() {
        if (level == null || level.isClientSide)
            return;

        boolean allOverHalf = energies.size() == 6;
        if (allOverHalf) {
            for (AbyssEnergyData data : energies) {
                if (data.amountOrZero() < 5000) {
                    allOverHalf = false;
                    break;
                }
            }
        }

        BlockState state = getBlockState();
        boolean isActive = state.getValue(AbyssPortal.ACTIVE);

        if (allOverHalf != isActive) {
            level.setBlock(worldPosition, state.setValue(AbyssPortal.ACTIVE, allOverHalf), 3);
        }

        if (allOverHalf) {
            for (int i = 0; i < energies.size(); i++) {
                AbyssEnergyData data = energies.get(i);
                long nextAmount = Math.max(0, data.amountOrZero() - 5);
                energies.set(i, new AbyssEnergyData(data.energyId(), nextAmount));
            }
            markDirtyAndSync();
        }
    }

    public void dropHeldItems() {
        if (level == null)
            return;
        for (var held : heldItems) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                    held.getHeldItem());
        }
    }
}
