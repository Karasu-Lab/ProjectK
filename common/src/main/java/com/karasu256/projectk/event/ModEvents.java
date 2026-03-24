package com.karasu256.projectk.event;

import com.karasu256.projectk.entity.AbyssEnergyEntity;
import com.karasu256.projectk.registry.EntitiesRegistry;
import com.karasu256.projectk.energy.IAbyssEnergy;
import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class ModEvents {
    public static void init() {
        ProjectKEntityEvents.LIVING_DEATH_AROUND_ANY_BLOCK.register(new LivingEntityDeathAroundWitherLose());
    }

    private static class LivingEntityDeathAroundWitherLose implements LivingEntityDeathAroundBlock {
        @Override
        public int getRadius() {
            return 5;
        }

        @Override
        public boolean shouldTrigger(LivingEntityDeathAroundBlock event, int radius, LivingEntity entity, @NotNull BlockPos entityPos, Level level) {
            for (BlockPos pos : BlockPos.betweenClosed(entityPos.offset(-radius, -radius, -radius), entityPos.offset(radius, radius, radius))) {
                if (level.getBlockState(pos).is(event.getBlock())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public EventResult die(LivingEntity entity, BlockPos pos, Level level) {
            if (!level.isClientSide) {
                AbyssEnergyEntity aeEntity = new AbyssEnergyEntity(EntitiesRegistry.ABYSS_ENERGY_ENTITY.get(), level);
                aeEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
                aeEntity.setEnergy(IAbyssEnergy.calculateEnergy(entity));
                level.addFreshEntity(aeEntity);
            }
            return EventResult.pass();
        }

        @Override
        public Block getBlock() {
            return Blocks.WITHER_ROSE;
        }
    }
}
