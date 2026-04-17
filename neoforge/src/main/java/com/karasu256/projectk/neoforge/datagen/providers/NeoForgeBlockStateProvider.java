package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.custom.AbyssLaserEmitter;
import com.karasu256.projectk.block.custom.AbyssPortal;
import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import com.karasu256.projectk.datagen.providers.CommonItemModelProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
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
        itemModels().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath())
                .parent(new ModelFile.UncheckedModelFile(
                        ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + name)));
    }

    @Override
    public void simpleBlockItem(Block block, String modelPath) {
        ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
        itemModels().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath())
                .parent(new ModelFile.UncheckedModelFile(modelLocation));
    }

    @Override
    public void existingModelBlock(Block block, String modelPath) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.parse(modelPath))).build());
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

        ModelFile centerModel = models().getBuilder(id + "_center")
                .parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("particle", baseTex).texture("all", baseTex).element().from(5, 5, 5).to(11, 11, 11)
                .allFaces((d, f) -> f.uvs(0, 0, 16, 16).texture("#all")).end();

        ModelFile sideModel = models().getBuilder(id + "_side")
                .parent(models().getExistingFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("particle", sideTex).texture("all", sideTex).element().from(5, 5, 0).to(11, 11, 5)
                .allFaces((d, f) -> f.uvs(0, 0, 16, 16).texture("#all")).end();

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
        ResourceLocation pulse = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + baseName + "_pulse");
        ResourceLocation dc = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + baseName + "_dc");

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(AbyssLaserEmitter.FACING);
            AbyssLaserEmitter.Mode mode = state.getValue(AbyssLaserEmitter.MODE);
            return ConfiguredModel.builder()
                    .modelFile(new ModelFile.UncheckedModelFile(mode == AbyssLaserEmitter.Mode.PULSE ? pulse : dc))
                    .rotationX(facing == Direction.DOWN ? 90 : facing == Direction.UP ? 270 : 0)
                    .rotationY(facing.getAxis().isVertical() ? 0 : (((int) facing.toYRot()) + 180) % 360).build();
        });
    }

    @Override
    public void activeBlock(Block block, String modelPath, String activeModelPath) {
        ModelFile inactive = new ModelFile.UncheckedModelFile(ResourceLocation.parse(modelPath));
        ModelFile active = new ModelFile.UncheckedModelFile(ResourceLocation.parse(activeModelPath));
        getVariantBuilder(block).forAllStates(state -> {
            boolean isActive = state.getValue(AbyssPortal.ACTIVE);
            return ConfiguredModel.builder().modelFile(isActive ? active : inactive).build();
        });
    }

    @Override
    public void fullBlockByFamily(Block block) {
        this.simpleBlock(block);
    }

    @Override
    public void stairsBlock(StairBlock block, Block fullBlock) {
        stairsBlock(block, blockTexture(fullBlock));
        this.simpleBlockItem(block);
    }

    @Override
    public void slabBlock(SlabBlock block, Block fullBlock) {
        slabBlock(block, BuiltInRegistries.BLOCK.getKey(fullBlock), blockTexture(fullBlock));
        this.simpleBlockItem(block);
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
