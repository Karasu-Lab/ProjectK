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
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

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
        TextureMapping textures = TextureMapping.cube(block);
        ResourceLocation modelLocation = ModelTemplates.CUBE_ALL.create(block, textures, this.blockModelGenerators.modelOutput);
        this.blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
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
    public void cubeBottomTop(String name, String side, String bottom, String top) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.SIDE, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + side))
                .put(TextureSlot.BOTTOM, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + bottom))
                .put(TextureSlot.TOP, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + top));
        ModelTemplates.CUBE_BOTTOM_TOP.create(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + name), textures, this.blockModelGenerators.modelOutput);
    }

    @Override
    public void simpleBlockItem(Block block) {
        this.blockModelGenerators.delegateItemModel(block, ModelLocationUtils.getModelLocation(block));
    }

    @Override
    public void existingModelBlock(Block block, String modelPath) {
        ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
        this.blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
    }

    @Override
    public void existingModelBlockAllStates(Block block, String modelPath) {
        ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
        this.blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
    }

    @Override
    public void multipartCable(Block block, String id) {
        ResourceLocation centerModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_center");
        ResourceLocation sideModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_side");
        MultiPartGenerator generator = MultiPartGenerator.multiPart(block)
                .with(Variant.variant().with(VariantProperties.MODEL, centerModel));

        addSide(generator, com.karasu256.projectk.block.custom.AbyssEnergyCable.NORTH, sideModel, VariantProperties.Rotation.R0, null);
        addSide(generator, com.karasu256.projectk.block.custom.AbyssEnergyCable.EAST, sideModel, VariantProperties.Rotation.R90, null);
        addSide(generator, com.karasu256.projectk.block.custom.AbyssEnergyCable.SOUTH, sideModel, VariantProperties.Rotation.R180, null);
        addSide(generator, com.karasu256.projectk.block.custom.AbyssEnergyCable.WEST, sideModel, VariantProperties.Rotation.R270, null);
        addSide(generator, com.karasu256.projectk.block.custom.AbyssEnergyCable.UP, sideModel, null, VariantProperties.Rotation.R270);
        addSide(generator, com.karasu256.projectk.block.custom.AbyssEnergyCable.DOWN, sideModel, null, VariantProperties.Rotation.R90);

        this.blockModelGenerators.blockStateOutput.accept(generator);
    }

    private void addSide(MultiPartGenerator generator, BooleanProperty prop, ResourceLocation model, VariantProperties.Rotation yRot, VariantProperties.Rotation xRot) {
        Variant variant = Variant.variant().with(VariantProperties.MODEL, model);
        if (yRot != null) {
            variant = variant.with(VariantProperties.Y_ROT, yRot);
        }
        if (xRot != null) {
            variant = variant.with(VariantProperties.X_ROT, xRot);
        }
        generator.with(Condition.condition().term(prop, true), variant);
    }

    @Override
    public void simpleItem(RegistrySupplier<Item> item) {
        this.itemModelGenerators.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
    }
}
