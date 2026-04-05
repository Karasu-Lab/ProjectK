package com.karasu256.projectk.fabric.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import com.karasu256.projectk.datagen.providers.CommonItemModelProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
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
        ResourceLocation modelLocation = ModelTemplates.CUBE_ALL.create(block, textures,
                this.blockModelGenerators.modelOutput);
        this.blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
    }

    @Override
    public void cubeBottomTop(Block block, String base, String side, String bottom, String top) {
        TextureMapping textures = new TextureMapping().put(TextureSlot.SIDE,
                        ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + side))
                .put(TextureSlot.BOTTOM,
                        ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + bottom))
                .put(TextureSlot.TOP,
                        ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + top));
        ResourceLocation modelLocation = ModelTemplates.CUBE_BOTTOM_TOP.create(block, textures,
                this.blockModelGenerators.modelOutput);
        this.blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
    }

    @Override
    public void cubeBottomTop(String name, String side, String bottom, String top) {
        TextureMapping textures = new TextureMapping().put(TextureSlot.SIDE,
                        ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + side))
                .put(TextureSlot.BOTTOM, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + bottom))
                .put(TextureSlot.TOP, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + top));
        ModelTemplates.CUBE_BOTTOM_TOP.create(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + name),
                textures, this.blockModelGenerators.modelOutput);
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
        ResourceLocation centerModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/" + id + "_center");
        ResourceLocation sideModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_side");
        ResourceLocation inputModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_input");
        ResourceLocation outputModel = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/" + id + "_output");

        ResourceLocation baseTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/multipart/" + id);
        ResourceLocation sideTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/multipart/" + id + "_vertical");
        ResourceLocation inputTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/multipart/" + id + "_input");
        ResourceLocation outputTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/multipart/" + id + "_output");

        this.blockModelGenerators.modelOutput.accept(centerModel,
                () -> createCableJson(baseTex, new float[][]{{5, 5, 5, 11, 11, 11}}, new float[][]{{0, 0, 16, 16}}));
        this.blockModelGenerators.modelOutput.accept(sideModel,
                () -> createCableJson(sideTex, new float[][]{{5, 5, 0, 11, 11, 5}}, new float[][]{{0, 0, 16, 16}}));
        this.blockModelGenerators.modelOutput.accept(inputModel, () -> createCableJson(inputTex,
                new float[][]{{4, 4, 0, 12, 12, 2}, {6, 6, 2, 10, 10, 4}, {5, 5, 4, 11, 11, 5}},
                new float[][]{{4, 0, 12, 2}, {6, 2, 10, 4}, {5, 4, 11, 5}}));
        this.blockModelGenerators.modelOutput.accept(outputModel, () -> createCableJson(outputTex,
                new float[][]{{7, 7, 0, 9, 9, 2}, {6, 6, 2, 10, 10, 3}, {5, 5, 3, 11, 11, 5}},
                new float[][]{{7, 0, 9, 2}, {6, 2, 10, 3}, {5, 3, 11, 5}}));

        MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

        generator.with(Variant.variant().with(VariantProperties.MODEL, centerModel));

        addSide(generator, Direction.NORTH, sideModel, VariantProperties.Rotation.R0, VariantProperties.Rotation.R0);
        addSide(generator, Direction.EAST, sideModel, VariantProperties.Rotation.R90, VariantProperties.Rotation.R0);
        addSide(generator, Direction.SOUTH, sideModel, VariantProperties.Rotation.R180, VariantProperties.Rotation.R0);
        addSide(generator, Direction.WEST, sideModel, VariantProperties.Rotation.R270, VariantProperties.Rotation.R0);
        addSide(generator, Direction.UP, sideModel, VariantProperties.Rotation.R0, VariantProperties.Rotation.R270);
        addSide(generator, Direction.DOWN, sideModel, VariantProperties.Rotation.R0, VariantProperties.Rotation.R90);

        for (Direction dir : Direction.values()) {
            generator.with(Condition.condition().term(AbyssEnergyCable.FACING, dir)
                    .term(AbyssEnergyCable.MODE, AbyssEnergyCable.ConnectionMode.INPUT), getVariant(inputModel, dir));
            generator.with(Condition.condition().term(AbyssEnergyCable.FACING, dir)
                    .term(AbyssEnergyCable.MODE, AbyssEnergyCable.ConnectionMode.OUTPUT), getVariant(outputModel, dir));
        }

        this.blockModelGenerators.blockStateOutput.accept(generator);
    }

    private Variant getVariant(ResourceLocation model, Direction dir) {
        Variant variant = Variant.variant().with(VariantProperties.MODEL, model);
        VariantProperties.Rotation yRot = getRotationYFabric(dir);
        VariantProperties.Rotation xRot = getRotationXFabric(dir);
        if (yRot != VariantProperties.Rotation.R0)
            variant = variant.with(VariantProperties.Y_ROT, yRot);
        if (xRot != VariantProperties.Rotation.R0)
            variant = variant.with(VariantProperties.X_ROT, xRot);
        return variant;
    }

    private VariantProperties.Rotation getRotationYFabric(Direction dir) {
        return switch (dir) {
            case EAST -> VariantProperties.Rotation.R90;
            case SOUTH -> VariantProperties.Rotation.R180;
            case WEST -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }

    private VariantProperties.Rotation getRotationXFabric(Direction dir) {
        return switch (dir) {
            case UP -> VariantProperties.Rotation.R270;
            case DOWN -> VariantProperties.Rotation.R90;
            default -> VariantProperties.Rotation.R0;
        };
    }

    private void addSide(MultiPartGenerator generator, Direction dir, ResourceLocation model, VariantProperties.Rotation yRot, VariantProperties.Rotation xRot) {
        BooleanProperty prop = getPropertyFor(dir);
        for (Direction f : Direction.values()) {
            Variant variant = Variant.variant().with(VariantProperties.MODEL, model);
            if (yRot != VariantProperties.Rotation.R0)
                variant = variant.with(VariantProperties.Y_ROT, yRot);
            if (xRot != VariantProperties.Rotation.R0)
                variant = variant.with(VariantProperties.X_ROT, xRot);

            if (f == dir) {
                generator.with(Condition.condition().term(prop, true).term(AbyssEnergyCable.FACING, f)
                        .term(AbyssEnergyCable.MODE, AbyssEnergyCable.ConnectionMode.CONNECTED), variant);
            } else {
                generator.with(Condition.condition().term(prop, true).term(AbyssEnergyCable.FACING, f), variant);
            }
        }
    }

    private BooleanProperty getPropertyFor(Direction dir) {
        return switch (dir) {
            case NORTH -> AbyssEnergyCable.NORTH;
            case SOUTH -> AbyssEnergyCable.SOUTH;
            case EAST -> AbyssEnergyCable.EAST;
            case WEST -> AbyssEnergyCable.WEST;
            case UP -> AbyssEnergyCable.UP;
            case DOWN -> AbyssEnergyCable.DOWN;
        };
    }

    @Override
    public void simpleItem(RegistrySupplier<Item> item) {
        this.itemModelGenerators.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
    }

    private JsonObject createCableJson(ResourceLocation texture, float[][] boxes, float[][] uvs) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", texture.toString());
        textures.addProperty("all", texture.toString());
        json.add("textures", textures);
        JsonArray elements = new JsonArray();
        for (int i = 0; i < boxes.length; i++) {
            JsonObject element = new JsonObject();
            JsonArray from = new JsonArray();
            from.add(boxes[i][0]);
            from.add(boxes[i][1]);
            from.add(boxes[i][2]);
            element.add("from", from);
            JsonArray to = new JsonArray();
            to.add(boxes[i][3]);
            to.add(boxes[i][4]);
            to.add(boxes[i][5]);
            element.add("to", to);
            JsonObject faces = new JsonObject();
            for (Direction dir : Direction.values()) {
                JsonObject face = new JsonObject();
                face.addProperty("texture", "#all");
                JsonArray uv = new JsonArray();
                uv.add(uvs[i][0]);
                uv.add(uvs[i][1]);
                uv.add(uvs[i][2]);
                uv.add(uvs[i][3]);
                face.add("uv", uv);
                faces.add(dir.getSerializedName(), face);
            }
            element.add("faces", faces);
            elements.add(element);
        }
        json.add("elements", elements);
        return json;
    }
}
