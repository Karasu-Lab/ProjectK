package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.custom.AbyssEnergyCable.ConnectionMode;
import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import com.karasu256.projectk.datagen.providers.CommonItemModelProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
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
        simpleBlock(block, models().cubeBottomTop(BuiltInRegistries.BLOCK.getKey(block).getPath(), ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + side), ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + bottom), ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + base + "/" + top)));
    }

    @Override
    public void cubeBottomTop(String name, String side, String bottom, String top) {
        models().withExistingParent("block/" + name, "minecraft:block/cube_bottom_top").texture("side", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + side)).texture("bottom", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + bottom)).texture("top", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + top));
    }

    @Override
    public void simpleBlockItem(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        itemModels().getBuilder(name).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + name)));
    }

    @Override
    public void existingModelBlock(Block block, String modelPath) {
        ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
        simpleBlock(block, new ModelFile.UncheckedModelFile(modelLocation));
    }

    @Override
    public void existingModelBlockAllStates(Block block, String modelPath) {
        ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(new ModelFile.UncheckedModelFile(modelLocation)).build());
    }

    @Override
    public void multipartCable(Block block, String id) {
        ModelFile centerModel = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_center"));
        ModelFile sideModel = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_side"));
        ModelFile sideInputModel = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_side_input"));
        ModelFile sideOutputModel = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + id + "_side_output"));
        
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        addCenter(builder, AbyssEnergyCable.NORTH, centerModel);
        addCenter(builder, AbyssEnergyCable.EAST, centerModel);
        addCenter(builder, AbyssEnergyCable.SOUTH, centerModel);
        addCenter(builder, AbyssEnergyCable.WEST, centerModel);
        addCenter(builder, AbyssEnergyCable.UP, centerModel);
        addCenter(builder, AbyssEnergyCable.DOWN, centerModel);

        addSide(builder, AbyssEnergyCable.NORTH, sideModel, ConnectionMode.CONNECTED, 0, 0);
        addSide(builder, AbyssEnergyCable.NORTH, sideInputModel, ConnectionMode.INPUT, 0, 0);
        addSide(builder, AbyssEnergyCable.NORTH, sideOutputModel, ConnectionMode.OUTPUT, 0, 0);

        addSide(builder, AbyssEnergyCable.EAST, sideModel, ConnectionMode.CONNECTED, 90, 0);
        addSide(builder, AbyssEnergyCable.EAST, sideInputModel, ConnectionMode.INPUT, 90, 0);
        addSide(builder, AbyssEnergyCable.EAST, sideOutputModel, ConnectionMode.OUTPUT, 90, 0);

        addSide(builder, AbyssEnergyCable.SOUTH, sideModel, ConnectionMode.CONNECTED, 180, 0);
        addSide(builder, AbyssEnergyCable.SOUTH, sideInputModel, ConnectionMode.INPUT, 180, 0);
        addSide(builder, AbyssEnergyCable.SOUTH, sideOutputModel, ConnectionMode.OUTPUT, 180, 0);

        addSide(builder, AbyssEnergyCable.WEST, sideModel, ConnectionMode.CONNECTED, 270, 0);
        addSide(builder, AbyssEnergyCable.WEST, sideInputModel, ConnectionMode.INPUT, 270, 0);
        addSide(builder, AbyssEnergyCable.WEST, sideOutputModel, ConnectionMode.OUTPUT, 270, 0);

        addSide(builder, AbyssEnergyCable.UP, sideModel, ConnectionMode.CONNECTED, 0, 270);
        addSide(builder, AbyssEnergyCable.UP, sideInputModel, ConnectionMode.INPUT, 0, 270);
        addSide(builder, AbyssEnergyCable.UP, sideOutputModel, ConnectionMode.OUTPUT, 0, 270);

        addSide(builder, AbyssEnergyCable.DOWN, sideModel, ConnectionMode.CONNECTED, 0, 90);
        addSide(builder, AbyssEnergyCable.DOWN, sideInputModel, ConnectionMode.INPUT, 0, 90);
        addSide(builder, AbyssEnergyCable.DOWN, sideOutputModel, ConnectionMode.OUTPUT, 0, 90);
    }

    private void addCenter(MultiPartBlockStateBuilder builder, EnumProperty<ConnectionMode> prop, ModelFile model) {
        builder.part().modelFile(model).addModel().condition(prop, ConnectionMode.NONE).end();
    }

    private void addSide(MultiPartBlockStateBuilder builder, EnumProperty<ConnectionMode> prop, ModelFile model, ConnectionMode mode, int yRot, int xRot) {
        builder.part().modelFile(model).rotationY(yRot).rotationX(xRot).addModel().condition(prop, mode).end();
    }

    @Override
    public void simpleItem(@NotNull RegistrySupplier<Item> item) {
        itemModels().withExistingParent(item.getId().getPath(), "item/generated").texture("layer0", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "item/" + item.getId().getPath()));
    }
}
