package com.karasu256.projectk.fabric.datagen.providers;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.custom.AbyssEnergyCable.ConnectionMode;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;

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
        ResourceLocation sideInputModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_side_input");
        ResourceLocation sideOutputModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_side_output");
        
        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        addCenter(generator, AbyssEnergyCable.NORTH, centerModel);
        addCenter(generator, AbyssEnergyCable.EAST, centerModel);
        addCenter(generator, AbyssEnergyCable.SOUTH, centerModel);
        addCenter(generator, AbyssEnergyCable.WEST, centerModel);
        addCenter(generator, AbyssEnergyCable.UP, centerModel);
        addCenter(generator, AbyssEnergyCable.DOWN, centerModel);

        addSide(generator, AbyssEnergyCable.NORTH, sideModel, ConnectionMode.CONNECTED, VariantProperties.Rotation.R0, null);
        addSide(generator, AbyssEnergyCable.NORTH, sideInputModel, ConnectionMode.INPUT, VariantProperties.Rotation.R0, null);
        addSide(generator, AbyssEnergyCable.NORTH, sideOutputModel, ConnectionMode.OUTPUT, VariantProperties.Rotation.R0, null);

        addSide(generator, AbyssEnergyCable.EAST, sideModel, ConnectionMode.CONNECTED, VariantProperties.Rotation.R90, null);
        addSide(generator, AbyssEnergyCable.EAST, sideInputModel, ConnectionMode.INPUT, VariantProperties.Rotation.R90, null);
        addSide(generator, AbyssEnergyCable.EAST, sideOutputModel, ConnectionMode.OUTPUT, VariantProperties.Rotation.R90, null);

        addSide(generator, AbyssEnergyCable.SOUTH, sideModel, ConnectionMode.CONNECTED, VariantProperties.Rotation.R180, null);
        addSide(generator, AbyssEnergyCable.SOUTH, sideInputModel, ConnectionMode.INPUT, VariantProperties.Rotation.R180, null);
        addSide(generator, AbyssEnergyCable.SOUTH, sideOutputModel, ConnectionMode.OUTPUT, VariantProperties.Rotation.R180, null);

        addSide(generator, AbyssEnergyCable.WEST, sideModel, ConnectionMode.CONNECTED, VariantProperties.Rotation.R270, null);
        addSide(generator, AbyssEnergyCable.WEST, sideInputModel, ConnectionMode.INPUT, VariantProperties.Rotation.R270, null);
        addSide(generator, AbyssEnergyCable.WEST, sideOutputModel, ConnectionMode.OUTPUT, VariantProperties.Rotation.R270, null);

        addSide(generator, AbyssEnergyCable.UP, sideModel, ConnectionMode.CONNECTED, null, VariantProperties.Rotation.R270);
        addSide(generator, AbyssEnergyCable.UP, sideInputModel, ConnectionMode.INPUT, null, VariantProperties.Rotation.R270);
        addSide(generator, AbyssEnergyCable.UP, sideOutputModel, ConnectionMode.OUTPUT, null, VariantProperties.Rotation.R270);

        addSide(generator, AbyssEnergyCable.DOWN, sideModel, ConnectionMode.CONNECTED, null, VariantProperties.Rotation.R90);
        addSide(generator, AbyssEnergyCable.DOWN, sideInputModel, ConnectionMode.INPUT, null, VariantProperties.Rotation.R90);
        addSide(generator, AbyssEnergyCable.DOWN, sideOutputModel, ConnectionMode.OUTPUT, null, VariantProperties.Rotation.R90);

        this.blockModelGenerators.blockStateOutput.accept(generator);
    }

    private void addCenter(MultiPartGenerator generator, EnumProperty<ConnectionMode> prop, ResourceLocation model) {
        Variant variant = Variant.variant().with(VariantProperties.MODEL, model);
        generator.with(Condition.condition().term(prop, ConnectionMode.NONE), variant);
    }

    private void addSide(MultiPartGenerator generator, EnumProperty<ConnectionMode> prop, ResourceLocation model, ConnectionMode mode, VariantProperties.Rotation yRot, VariantProperties.Rotation xRot) {
        Variant variant = Variant.variant().with(VariantProperties.MODEL, model);
        if (yRot != null) {
            variant = variant.with(VariantProperties.Y_ROT, yRot);
        }
        if (xRot != null) {
            variant = variant.with(VariantProperties.X_ROT, xRot);
        }
        generator.with(Condition.condition().term(prop, mode), variant);
    }

    @Override
    public void simpleItem(RegistrySupplier<Item> item) {
        this.itemModelGenerators.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
    }
}
