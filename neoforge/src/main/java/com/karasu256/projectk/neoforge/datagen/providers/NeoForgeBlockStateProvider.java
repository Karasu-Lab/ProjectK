package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.custom.AbyssLaserEmitter;
import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import com.karasu256.projectk.datagen.providers.CommonItemModelProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class NeoForgeBlockStateProvider extends BlockStateProvider implements CommonBlockStateProvider.Generator, CommonItemModelProvider.ItemGenerator {

    public NeoForgeBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, ProjectK.MOD_ID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        CommonBlockStateProvider.generate(this);
        CommonItemModelProvider.generate(this);
    }

    @Override
    public void simpleBlock(@NotNull Block block) {
        super.simpleBlock(block);
    }

    @Override
    public void cubeBottomTop(Block block, String base, String side, String bottom, String top) {
        simpleBlock(block, models().cubeBottomTop(BuiltInRegistries.BLOCK.getKey(block).getPath(),
                ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + side),
                ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + bottom),
                ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + top)));
    }

    @Override
    public void cubeBottomTop(String name, String side, String bottom, String top) {
        models().withExistingParent("block/" + name, "minecraft:block/cube_bottom_top")
                .texture("side", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + side))
                .texture("bottom", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + bottom))
                .texture("top", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + top));
    }

    @Override
    public void simpleBlockItem(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        if (block == ProjectKBlocks.ABYSS_LASER_EMITTER.get()) {
            name += "_pulse";
        }
        itemModels().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath()).parent(new ModelFile.UncheckedModelFile(
                ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + name)));
    }

    @Override
    public void existingModelBlock(Block block, String modelPath) {
        ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
        String name = modelLocation.getPath().replace("block/", "");
        simpleBlock(block, models().cubeAll(name, ResourceLocation.fromNamespaceAndPath(modelLocation.getNamespace(), "block/" + name)));
    }

    @Override
    public void existingModelBlockAllStates(Block block, String modelPath) {
        ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
        getVariantBuilder(block).forAllStates(
                state -> ConfiguredModel.builder().modelFile(new ModelFile.UncheckedModelFile(modelLocation)).build());
    }

    @Override
    public void multipartCable(Block block, String id) {
        ResourceLocation baseTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/multipart/" + id);
        ResourceLocation sideTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/multipart/" + id + "_vertical");
        ResourceLocation inputTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/multipart/" + id + "_input");
        ResourceLocation outputTex = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
                "block/multipart/" + id + "_output");

        ModelFile centerModel = models().getBuilder(id + "_center")
                .parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("particle", baseTex).texture("all", baseTex).element().from(5, 5, 5).to(11, 11, 11)
                .allFaces((d, f) -> f.uvs(0, 0, 16, 16).texture("#all")).end();

        ModelFile sideModel = models().getBuilder(id + "_side")
                .parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("particle", sideTex).texture("all", sideTex).element().from(5, 5, 0).to(11, 11, 5)
                .allFaces((d, f) -> f.uvs(0, 0, 16, 16).texture("#all")).end();

        ModelFile inputModel = models().getBuilder(id + "_input")
                .parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("particle", inputTex).texture("all", inputTex).element().from(4, 4, 0).to(12, 12, 2)
                .allFaces((d, f) -> f.uvs(4, 0, 12, 2).texture("#all")).end().element().from(6, 6, 2).to(10, 10, 4)
                .allFaces((d, f) -> f.uvs(6, 2, 10, 4).texture("#all")).end().element().from(5, 5, 4).to(11, 11, 5)
                .allFaces((d, f) -> f.uvs(5, 4, 11, 5).texture("#all")).end();

        ModelFile outputModel = models().getBuilder(id + "_output")
                .parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("particle", outputTex).texture("all", outputTex).element().from(7, 7, 0).to(9, 9, 2)
                .allFaces((d, f) -> f.uvs(7, 0, 9, 2).texture("#all")).end().element().from(6, 6, 2).to(10, 10, 3)
                .allFaces((d, f) -> f.uvs(6, 2, 10, 3).texture("#all")).end().element().from(5, 5, 3).to(11, 11, 5)
                .allFaces((d, f) -> f.uvs(5, 3, 11, 5).texture("#all")).end();

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        builder.part().modelFile(centerModel).addModel().end();

        for (Direction dir : Direction.values()) {
            int yRot = getRotationY(dir);
            int xRot = getRotationX(dir);
            builder.part().modelFile(sideModel).rotationY(yRot).rotationX(xRot).addModel()
                    .condition(AbyssEnergyCable.getConnectionPropertyFor(dir), true).end();
        }
    }

    @Override
    public void directionalModeBlock(Block block, String baseName) {
        ModelFile pulse = models().withExistingParent(baseName + "_pulse", "minecraft:block/orientable_with_bottom")
                .texture("top", "projectk:block/" + baseName + "_pulse")
                .texture("front", "projectk:block/" + baseName + "_pulse")
                .texture("side", "projectk:block/" + baseName + "_side")
                .texture("bottom", "projectk:block/" + baseName + "_back");

        ModelFile dc = models().withExistingParent(baseName + "_dc", "minecraft:block/orientable_with_bottom")
                .texture("top", "projectk:block/" + baseName + "_dc")
                .texture("front", "projectk:block/" + baseName + "_dc")
                .texture("side", "projectk:block/" + baseName + "_side")
                .texture("bottom", "projectk:block/" + baseName + "_back");

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(AbyssLaserEmitter.FACING);
            AbyssLaserEmitter.Mode mode = state.getValue(AbyssLaserEmitter.MODE);
            return ConfiguredModel.builder()
                    .modelFile(mode == AbyssLaserEmitter.Mode.PULSE ? pulse : dc)
                    .rotationX(facing == Direction.DOWN ? 90 : facing == Direction.UP ? 270 : 0)
                    .rotationY(facing.getAxis().isVertical() ? 0 : (((int) facing.toYRot()) + 180) % 360)
                    .build();
        });
    }

    private int getRotationY(Direction dir) {
        return switch (dir) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

    private int getRotationX(Direction dir) {
        return switch (dir) {
            case UP -> 270;
            case DOWN -> 90;
            default -> 0;
        };
    }


    @Override
    public void simpleItem(@NotNull RegistrySupplier<Item> item) {
        itemModels().withExistingParent(item.getId().getPath(), "item/generated").texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "item/" + item.getId().getPath()));
    }
}
