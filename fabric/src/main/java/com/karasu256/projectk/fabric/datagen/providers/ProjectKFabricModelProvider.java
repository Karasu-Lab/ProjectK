package com.karasu256.projectk.fabric.datagen.providers;

import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ProjectKFabricModelProvider extends FabricModelProvider implements CommonBlockStateProvider.Generator, CommonBlockStateProvider.ItemGenerator {

    private BlockModelGenerators blockModelGenerators;
    private ItemModelGenerators itemModelGenerators;

    public ProjectKFabricModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        this.blockModelGenerators = blockModelGenerators;
        CommonBlockStateProvider.generate(this);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        this.itemModelGenerators = itemModelGenerators;
        CommonBlockStateProvider.generateItems(this);
    }

    @Override
    public void simpleBlock(Block block) {
        this.blockModelGenerators.createTrivialCube(block);
    }

    @Override
    public void simpleBlockItem(Block block) {
    }

    @Override
    public void simpleItem(RegistrySupplier<Item> item) {
        this.itemModelGenerators.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
    }
}
