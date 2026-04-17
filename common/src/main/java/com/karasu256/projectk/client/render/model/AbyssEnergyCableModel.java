package com.karasu256.projectk.client.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.custom.AbyssEnergyCable.ConnectionMode;
import com.karasu256.projectk.block.entity.AbyssEnergyCableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mehvahdjukaar.moonlight.api.client.model.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AbyssEnergyCableModel implements CustomBakedModel {
    private final BakedModel center;
    private final BakedModel side;
    private final TextureAtlasSprite inputSprite;
    private final TextureAtlasSprite outputSprite;
    private final Map<Direction, List<BakedQuad>> cachedSideQuads = new EnumMap<>(Direction.class);

    public AbyssEnergyCableModel(BakedModel center, BakedModel side, TextureAtlasSprite inputSprite, TextureAtlasSprite outputSprite) {
        this.center = center;
        this.side = side;
        this.inputSprite = inputSprite;
        this.outputSprite = outputSprite;
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction direction, RandomSource randomSource, @Nullable RenderType renderType, ExtraModelData extraModelData) {
        if (direction != null) {
            return List.of();
        }

        List<BakedQuad> quads = new ArrayList<>(center.getQuads(state, null, randomSource));

        if (state.getValue(AbyssEnergyCable.NORTH))
            quads.addAll(getSideQuads(Direction.NORTH, state, randomSource));
        if (state.getValue(AbyssEnergyCable.SOUTH))
            quads.addAll(getSideQuads(Direction.SOUTH, state, randomSource));
        if (state.getValue(AbyssEnergyCable.EAST))
            quads.addAll(getSideQuads(Direction.EAST, state, randomSource));
        if (state.getValue(AbyssEnergyCable.WEST))
            quads.addAll(getSideQuads(Direction.WEST, state, randomSource));
        if (state.getValue(AbyssEnergyCable.UP))
            quads.addAll(getSideQuads(Direction.UP, state, randomSource));
        if (state.getValue(AbyssEnergyCable.DOWN))
            quads.addAll(getSideQuads(Direction.DOWN, state, randomSource));

        ConnectionMode[] modes = extraModelData.get(AbyssEnergyCableBlockEntity.MODES_KEY);
        if (modes != null) {
            for (Direction dir : Direction.values()) {
                ConnectionMode mode = modes[dir.ordinal()];
                if (mode != ConnectionMode.NONE) {
                    addIndicatorQuads(quads, dir, mode);
                }
            }
        }

        return quads;
    }

    @Override
    public TextureAtlasSprite getBlockParticle(ExtraModelData extraModelData) {
        return center.getParticleIcon();
    }

    private List<BakedQuad> getSideQuads(Direction dir, BlockState state, RandomSource randomSource) {
        return cachedSideQuads.computeIfAbsent(dir, d -> {
            List<BakedQuad> original = side.getQuads(state, null, randomSource);
            if (d == Direction.NORTH)
                return original;

            BakedQuadsTransformer transformer = BakedQuadsTransformer.create();
            Matrix4f mat = new Matrix4f();
            mat.translate(0.5f, 0.5f, 0.5f);
            switch (d) {
                case SOUTH -> mat.rotateY((float) Math.toRadians(180));
                case EAST -> mat.rotateY((float) Math.toRadians(270));
                case WEST -> mat.rotateY((float) Math.toRadians(90));
                case UP -> mat.rotateX((float) Math.toRadians(90));
                case DOWN -> mat.rotateX((float) Math.toRadians(-90));
            }
            mat.translate(-0.5f, -0.5f, -0.5f);
            transformer.applyingTransform(mat);
            return transformer.transformAll(original);
        });
    }

    private void addIndicatorQuads(List<BakedQuad> quads, Direction dir, ConnectionMode mode) {
        TextureAtlasSprite sprite = mode == ConnectionMode.INPUT ? inputSprite : outputSprite;

        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.5, 0.5, 0.5);
        switch (dir) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        }
        poseStack.translate(-0.5, -0.5, -0.5);

        float p = 0.002f;
        if (mode == ConnectionMode.INPUT) {
            drawBox(quads, poseStack, 4 / 16f - p, 4 / 16f - p, 0 / 16f - p, 12 / 16f + p, 12 / 16f + p, 2 / 16f + p,
                    sprite);
            drawBox(quads, poseStack, 6 / 16f - p, 6 / 16f - p, 2 / 16f - p, 10 / 16f + p, 10 / 16f + p, 4 / 16f + p,
                    sprite);
            drawBox(quads, poseStack, 5 / 16f - p, 5 / 16f - p, 4 / 16f - p, 11 / 16f + p, 11 / 16f + p, 5 / 16f + p,
                    sprite);
        } else {
            drawBox(quads, poseStack, 7 / 16f - p, 7 / 16f - p, 0 / 16f - p, 9 / 16f + p, 9 / 16f + p, 2 / 16f + p,
                    sprite);
            drawBox(quads, poseStack, 6 / 16f - p, 6 / 16f - p, 2 / 16f - p, 10 / 16f + p, 10 / 16f + p, 3 / 16f + p,
                    sprite);
            drawBox(quads, poseStack, 5 / 16f - p, 5 / 16f - p, 3 / 16f - p, 11 / 16f + p, 11 / 16f + p, 5 / 16f + p,
                    sprite);
        }
    }

    private void drawBox(List<BakedQuad> quads, PoseStack poseStack, float x1, float y1, float z1, float x2, float y2, float z2, TextureAtlasSprite sprite) {
        try (BakedQuadBuilder builder = BakedQuadBuilder.create(sprite, quads::add)) {
            builder.setAutoDirection();

            Matrix4f pose = poseStack.last().pose();

            float u1 = sprite.getU0();
            float u2 = sprite.getU1();
            float v1 = sprite.getV0();
            float v2 = sprite.getV1();

            builder.addVertex(pose, x1, y2, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setNormal(0, 0, -1);
            builder.addVertex(pose, x2, y2, z1).setColor(255, 255, 255, 255).setUv(u2, v1).setNormal(0, 0, -1);
            builder.addVertex(pose, x2, y1, z1).setColor(255, 255, 255, 255).setUv(u2, v2).setNormal(0, 0, -1);
            builder.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v2).setNormal(0, 0, -1);

            builder.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u1, v1).setNormal(0, 0, 1);
            builder.addVertex(pose, x1, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setNormal(0, 0, 1);
            builder.addVertex(pose, x1, y1, z2).setColor(255, 255, 255, 255).setUv(u2, v2).setNormal(0, 0, 1);
            builder.addVertex(pose, x2, y1, z2).setColor(255, 255, 255, 255).setUv(u1, v2).setNormal(0, 0, 1);

            builder.addVertex(pose, x2, y2, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setNormal(1, 0, 0);
            builder.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setNormal(1, 0, 0);
            builder.addVertex(pose, x2, y1, z2).setColor(255, 255, 255, 255).setUv(u2, v2).setNormal(1, 0, 0);
            builder.addVertex(pose, x2, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v2).setNormal(1, 0, 0);

            builder.addVertex(pose, x1, y2, z2).setColor(255, 255, 255, 255).setUv(u1, v1).setNormal(-1, 0, 0);
            builder.addVertex(pose, x1, y2, z1).setColor(255, 255, 255, 255).setUv(u2, v1).setNormal(-1, 0, 0);
            builder.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u2, v2).setNormal(-1, 0, 0);
            builder.addVertex(pose, x1, y1, z2).setColor(255, 255, 255, 255).setUv(u1, v2).setNormal(-1, 0, 0);

            builder.addVertex(pose, x1, y2, z2).setColor(255, 255, 255, 255).setUv(u1, v1).setNormal(0, 1, 0);
            builder.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setNormal(0, 1, 0);
            builder.addVertex(pose, x2, y2, z1).setColor(255, 255, 255, 255).setUv(u2, v2).setNormal(0, 1, 0);
            builder.addVertex(pose, x1, y2, z1).setColor(255, 255, 255, 255).setUv(u1, v2).setNormal(0, 1, 0);

            builder.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setNormal(0, -1, 0);
            builder.addVertex(pose, x2, y1, z1).setColor(255, 255, 255, 255).setUv(u2, v1).setNormal(0, -1, 0);
            builder.addVertex(pose, x2, y1, z2).setColor(255, 255, 255, 255).setUv(u2, v2).setNormal(0, -1, 0);
            builder.addVertex(pose, x1, y1, z2).setColor(255, 255, 255, 255).setUv(u1, v2).setNormal(0, -1, 0);
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean useAmbientOcclusion() {
        return center.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return center.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return center.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
        return center.getTransforms();
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
        return center.getOverrides();
    }

    public static class Loader implements CustomModelLoader {
        @Override
        public CustomGeometry deserialize(JsonObject json, JsonDeserializationContext context) {
            ResourceLocation centerModel = ResourceLocation.parse(json.get("center").getAsString());
            ResourceLocation sideModel = ResourceLocation.parse(json.get("side").getAsString());
            ResourceLocation inputTex = ResourceLocation.parse(json.get("input").getAsString());
            ResourceLocation outputTex = ResourceLocation.parse(json.get("output").getAsString());
            return new Geometry(centerModel, sideModel, inputTex, outputTex);
        }
    }

    private record Geometry(ResourceLocation centerModel, ResourceLocation sideModel, ResourceLocation inputTex,
                            ResourceLocation outputTex) implements CustomGeometry {
        @Override
        public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform) {
            BakedModel center = modelBaker.getModel(centerModel).bake(modelBaker, spriteGetter, transform);
            BakedModel side = modelBaker.getModel(sideModel).bake(modelBaker, spriteGetter, transform);
            TextureAtlasSprite input = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, inputTex));
            TextureAtlasSprite output = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, outputTex));
            return new AbyssEnergyCableModel(center, side, input, output);
        }
    }
}
