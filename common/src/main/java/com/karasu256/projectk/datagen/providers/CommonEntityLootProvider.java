package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.item.ProjectKItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class CommonEntityLootProvider implements LootTableSubProvider {
    private final HolderLookup.Provider registries;

    public CommonEntityLootProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(EntityType.WITHER.getDefaultLootTable(), LootTable.lootTable().setParamSet(LootContextParamSets.ENTITY).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.NETHER_STAR))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ProjectKItems.WITHER_BONE.get()).when(LootItemRandomChanceCondition.randomChance(0.05F)))));
    }
}
