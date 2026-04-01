package com.karasu256.projectk.compat.rei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class InBiomeInBlockCraftingCategory implements DisplayCategory<InBiomeInBlockCraftingDisplay> {
    private final CategoryIdentifier<InBiomeInBlockCraftingDisplay> id;
    private final ResourceLocation iconId;
    private final Component title;

    public InBiomeInBlockCraftingCategory(CategoryIdentifier<InBiomeInBlockCraftingDisplay> id, ResourceLocation iconId, Component title) {
        this.id = id;
        this.iconId = iconId;
        this.title = title;
    }

    @Override
    public CategoryIdentifier<? extends InBiomeInBlockCraftingDisplay> getCategoryIdentifier() {
        return id;
    }

    @Override
    public int getDisplayWidth(InBiomeInBlockCraftingDisplay display) {
        return 170;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public Renderer getIcon() {
        return new BiomeIconRenderer(iconId);
    }

    @Override
    public List<Widget> setupDisplay(InBiomeInBlockCraftingDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 75, bounds.getCenterY() - 8);
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 20, startPoint.y)).entries(display.getInputEntries().get(1)).markInput());
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 45, startPoint.y)));

        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            List<BlockState> states = resolveBlockStates(display);
            BlockState state = states.isEmpty() ? Blocks.AIR.defaultBlockState() : states.get((int) (System.currentTimeMillis() / 1500 % states.size()));
            renderBlock(graphics, state, startPoint.x + 75, startPoint.y, 16, 16);
        }));

        widgets.add(Widgets.createArrow(new Point(startPoint.x + 100, startPoint.y)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 130, startPoint.y)).entries(display.getOutputEntries().get(0)).markOutput());

        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.y + 5), display.getRequirementText()).noShadow().color(0xFF555555).centered());

        return widgets;
    }

    private static List<BlockState> resolveBlockStates(InBiomeInBlockCraftingDisplay display) {
        var level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        ResourceLocation tagId = display.getBlockTagId();
        TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tagId);
        var registry = level.registryAccess().registryOrThrow(Registries.BLOCK);
        var tag = registry.getTag(tagKey).orElse(null);
        if (tag == null) return List.of();
        return tag.stream().map(holder -> holder.value().defaultBlockState()).toList();
    }

    private static class BiomeIconRenderer implements Renderer {
        private final ResourceLocation iconId;

        private BiomeIconRenderer(ResourceLocation iconId) {
            this.iconId = iconId;
        }

        @Override
        public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
            var manager = Minecraft.getInstance().getResourceManager();
            if (manager.getResource(iconId).isPresent()) {
                RenderSystem.setShaderTexture(0, iconId);
                graphics.blit(iconId, bounds.x, bounds.y, 0, 0, bounds.width, bounds.height, bounds.width, bounds.height);
            } else {
                EntryStacks.of(Items.WITHER_ROSE).render(graphics, bounds, mouseX, mouseY, delta);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void renderBlock(GuiGraphics guiGraphics, BlockState state, int x, int y, int width, int height) {
        if (!state.getFluidState().isEmpty()) {
            renderFluid(guiGraphics, state, x, y, width, height);
            return;
        }
        RenderSystem.runAsFancy(() -> {
            var blockRenderer = Minecraft.getInstance().getBlockRenderer();
            var model = blockRenderer.getBlockModel(state);
            RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(state);

            renderType.setupRenderState();
            RenderSystem.disableDepthTest();

            Matrix4fStack worldMatStack = RenderSystem.getModelViewStack();
            worldMatStack.pushMatrix();
            worldMatStack.mul(guiGraphics.pose().last().pose());
            worldMatStack.translate(x, y, 0);
            worldMatStack.translate(width / 2.f, height / 2.f, 0);
            worldMatStack.scale(width, height, 1);

            setupOrthographicProjection(worldMatStack);

            var builder = Tesselator.getInstance().begin(renderType.mode(), renderType.format());
            blockRenderer.getModelRenderer().tesselateBlock(new FakeWorld(state), model, state, BlockPos.ZERO, new PoseStack(), builder, true, net.minecraft.util.RandomSource.create(), state.getSeed(BlockPos.ZERO), OverlayTexture.NO_OVERLAY);
            var meshData = builder.build();
            if (meshData != null) {
                BufferUploader.drawWithShader(meshData);
            }

            renderType.clearRenderState();
            worldMatStack.popMatrix();
            RenderSystem.applyModelViewMatrix();
        });
    }

    private static void renderFluid(GuiGraphics guiGraphics, BlockState state, int x, int y, int width, int height) {
        RenderSystem.runAsFancy(() -> {
            var fluidState = state.getFluidState();
            var blockRenderer = Minecraft.getInstance().getBlockRenderer();
            var renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);

            renderType.setupRenderState();
            RenderSystem.disableDepthTest();

            Matrix4fStack worldMatStack = RenderSystem.getModelViewStack();
            worldMatStack.pushMatrix();
            worldMatStack.mul(guiGraphics.pose().last().pose());
            worldMatStack.translate(x, y, 0);
            FogRenderer.setupNoFog();
            worldMatStack.translate(width / 2.f, height / 2.f, 0);
            worldMatStack.scale(width, height, 1);

            setupOrthographicProjection(worldMatStack);

            var builder = Tesselator.getInstance().begin(renderType.mode(), renderType.format());
            blockRenderer.renderLiquid(BlockPos.ZERO, new FakeWorld(state), builder, fluidState.createLegacyBlock(), fluidState);
            var meshData = builder.build();
            if (meshData != null) {
                BufferUploader.drawWithShader(meshData);
            }

            renderType.clearRenderState();
            worldMatStack.popMatrix();
            RenderSystem.applyModelViewMatrix();
        });
    }

    private static void setupOrthographicProjection(Matrix4fStack worldMatStack) {
        float angle = 36;
        float rotation = 45;
        worldMatStack.scale(1, 1, -1);
        worldMatStack.rotate(new Quaternionf().rotationY(Mth.DEG_TO_RAD * -180));
        Quaternionf flip = new Quaternionf().rotationZ(Mth.DEG_TO_RAD * 180);
        flip.mul(new Quaternionf().rotationX(Mth.DEG_TO_RAD * angle));
        Quaternionf rotate = new Quaternionf().rotationY(Mth.DEG_TO_RAD * rotation);
        worldMatStack.rotate(flip);
        worldMatStack.rotate(rotate);
        worldMatStack.translate(-0.5f, -0.5f, -0.5f);
        RenderSystem.applyModelViewMatrix();
    }

    private record FakeWorld(BlockState state) implements BlockAndTintGetter {
        @Override
        public float getShade(Direction direction, boolean bl) {
            return 1.0f;
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return null;
        }

        @Override
        public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
            return 15;
        }

        @Override
        public int getRawBrightness(BlockPos blockPos, int i) {
            return 15;
        }

        @Override
        public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
            var level = Minecraft.getInstance().level;
            if (level != null) {
                var biome = level.getBiome(blockPos);
                return colorResolver.getColor(biome.value(), 0, 0);
            }
            return -1;
        }

        @Override
        public BlockEntity getBlockEntity(BlockPos blockPos) {
            return null;
        }

        @Override
        @NotNull
        public BlockState getBlockState(@NotNull BlockPos blockPos) {
            return blockPos.equals(BlockPos.ZERO) ? state : Blocks.AIR.defaultBlockState();
        }

        @Override
        @NotNull
        public FluidState getFluidState(BlockPos blockPos) {
            return blockPos.equals(BlockPos.ZERO) ? state.getFluidState() : Fluids.EMPTY.defaultFluidState();
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getMinBuildHeight() {
            return 0;
        }
    }
}
