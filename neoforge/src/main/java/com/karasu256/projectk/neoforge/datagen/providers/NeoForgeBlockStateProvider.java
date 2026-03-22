package com.karasu256.projectk.neoforge.datagen.providers;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.datagen.providers.CommonBlockStateProvider;
import net.minecraft.data.PackOutput;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class NeoForgeBlockStateProvider extends BlockStateProvider implements CommonBlockStateProvider.Generator, CommonBlockStateProvider.ItemGenerator {

    public NeoForgeBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, ProjectK.MOD_ID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        CommonBlockStateProvider.generate(this);
        CommonBlockStateProvider.generateItems(this);
    }

    @Override
    public void simpleBlock(@NotNull Block block) {
        super.simpleBlock(block);
    }

    @Override
    public void simpleBlockItem(@NotNull Block block) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
        itemModels().withExistingParent(name, ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "block/" + name));
    }

    @Override
    public void simpleItem(@NotNull RegistrySupplier<Item> item) {
        itemModels().withExistingParent(item.getId().getPath(), "item/generated")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID, "item/" + item.getId().getPath()));
    }
}
