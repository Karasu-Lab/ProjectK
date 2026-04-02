package com.karasu256.projectk.event;

import com.karasu256.projectk.entity.AbyssEnergyEntity;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.data.AbyssEnergySpawnRuleManager;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class ModEvents {
    public static void init() {
        ProjectKEntityEvents.LIVING_DEATH_AROUND_ANY_BLOCK.register(new LivingEntityDeathAroundWitherLose());

        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity.level().isClientSide) return EventResult.pass();

            BlockPos pos = entity.blockPosition();
            ResourceLocation energyId;
            long amount;

            var match = AbyssEnergySpawnRuleManager.findMatch(entity.level(), pos, entity);
            if (match.isPresent()) {
                var rule = match.get();
                energyId = rule.energyId();
                amount = rule.amount().resolve(entity);
            } else {
                energyId = AbyssEnergySpawnRuleManager.fallbackEnergyId();
                amount = AbyssEnergySpawnRuleManager.fallbackAmount(entity);
            }

            AbyssEnergyEntity aeEntity = new AbyssEnergyEntity(ProjectKEntities.ABYSS_ENERGY_ENTITY.get(), entity.level());
            aeEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
            aeEntity.setEnergy(amount);
            aeEntity.setEnergyId(energyId);
            entity.level().addFreshEntity(aeEntity);

            return EventResult.pass();
        });
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
            return EventResult.pass();
        }

        @Override
        public Block getBlock() {
            return Blocks.WITHER_ROSE;
        }
    }
}
