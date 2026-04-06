package com.karasu256.projectk.event;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.AbyssEnergySpawnRuleManager;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.enchant.ProjectKEnchantments;
import com.karasu256.projectk.entity.AbyssEnergyEntity;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.utils.Id;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LootEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

public class ModEvents {
    private static final ResourceLocation ABYSS_BOOSTER_ID = Id.id("abyss_booster_bonus");

    public static void init() {
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity.level().isClientSide)
                return EventResult.pass();

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

            AbyssEnergyEntity aeEntity = new AbyssEnergyEntity(ProjectKEntities.ABYSS_ENERGY_ENTITY.get(),
                    entity.level());
            aeEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
            aeEntity.setEnergy(amount);
            aeEntity.setEnergyId(energyId);
            entity.level().addFreshEntity(aeEntity);

            return EventResult.pass();
        });

        TickEvent.PLAYER_POST.register(ModEvents::applyAbyssBooster);

        LootEvent.MODIFY_LOOT_TABLE.register((key, context, builtin) -> {
            if (key.equals(BuiltInLootTables.ANCIENT_CITY)) {
                context.addPool(LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(
                                ResourceKey.create(Registries.LOOT_TABLE, Id.id("chests/ancient_city_injection")))));
            }
        });
    }

    private static void applyAbyssBooster(LivingEntity entity) {
        AttributeInstance attribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }
        attribute.removeModifier(ABYSS_BOOSTER_ID);

        var stack = entity.getMainHandItem();
        if (stack.isEmpty()) {
            return;
        }
        var enchantmentLookup = entity.level().holderLookup(Registries.ENCHANTMENT);
        int level = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                enchantmentLookup.getOrThrow(ProjectKEnchantments.ABYSS_BOOSTER_KEY), stack);
        if (level <= 0) {
            return;
        }
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (data == null) {
            return;
        }
        long bonusLevels = data.amount() / 10000L;
        if (bonusLevels <= 0) {
            return;
        }
        attribute.addTransientModifier(
                new AttributeModifier(ABYSS_BOOSTER_ID, (double) bonusLevels, AttributeModifier.Operation.ADD_VALUE));
    }

}
