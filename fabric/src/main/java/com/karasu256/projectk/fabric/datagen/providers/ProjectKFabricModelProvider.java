package com.karasu256.projectk.fabric.datagen.providers;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import com.karasu256.projectk.datagen.providers.CommonItemModelProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ProjectKFabricModelProvider extends FabricModelProvider implements CommonBlockStateProvider.Generator, CommonItemModelProvider.ItemGenerator {

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
        CommonItemModelProvider.generate(this);
    }

    @Override
    public void simpleBlock(Block block) {
        if (block == ProjectKBlocks.ABYSS_CORE.get()) {
            TextureMapping textures = TextureMapping.cube(block);
            ResourceLocation modelLocation = ModelTemplates.CUBE_ALL.create(block, textures, this.blockModelGenerators.modelOutput);
            this.blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
        } else {
            this.blockModelGenerators.createTrivialCube(block);
        }
    }

    @Override
    public void cubeBottomTop(Block block, String base, String side, String bottom, String top) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.SIDE, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + side))
                .put(TextureSlot.BOTTOM, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + bottom))
                .put(TextureSlot.TOP, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + top));
        ResourceLocation modelLocation = ModelTemplates.CUBE_BOTTOM_TOP.create(block, textures, this.blockModelGenerators.modelOutput);
        this.blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
    }

    @Override
    public void simpleBlockItem(Block block) {
        this.blockModelGenerators.delegateItemModel(block, ModelLocationUtils.getModelLocation(block));
    }

    @Override
    public void simpleItem(RegistrySupplier<Item> item) {
        this.itemModelGenerators.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
    }
}
