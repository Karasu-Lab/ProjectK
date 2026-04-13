package com.karasu256.projectk.entity;

import com.karasu256.projectk.block.entity.AbyssPortalBlockEntity;
import com.karasu256.projectk.particle.AbyssPortalParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AbyssPortalEnergyEntity extends AbyssEnergyEntity {
    public AbyssPortalEnergyEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean isValidTarget(BlockPos pos) {
        BlockEntity be = level().getBlockEntity(pos);
        return be instanceof AbyssPortalBlockEntity portal && portal.canAcceptEnergy(getEnergyId());
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            level().addParticle(new AbyssPortalParticleOptions(getEnergyId()),
                    getX() + (random.nextDouble() - 0.5) * 0.3, getY() + (random.nextDouble() - 0.5) * 0.3,
                    getZ() + (random.nextDouble() - 0.5) * 0.3, 0, 0, 0);
        }
    }
}
