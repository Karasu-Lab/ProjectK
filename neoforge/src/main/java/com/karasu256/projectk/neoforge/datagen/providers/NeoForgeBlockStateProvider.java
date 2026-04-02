package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import com.karasu256.projectk.datagen.providers.CommonItemModelProvider;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
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
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(new ModelFile.UncheckedModelFile(modelLocation))
                .build());
    }

    @Override
    public void simpleItem(@NotNull RegistrySupplier<Item> item) {
        itemModels().withExistingParent(item.getId().getPath(), "item/generated").texture("layer0", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "item/" + item.getId().getPath()));
    }
}
