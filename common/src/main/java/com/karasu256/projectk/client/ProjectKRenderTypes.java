package com.karasu256.projectk.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ProjectKRenderTypes extends RenderStateShard {
    public static final Function<ResourceLocation, RenderType> ABYSS_ENERGY = Util.memoize(texture -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new ShaderStateShard(ProjectKCoreShaders::getAbyssEnergyShader))
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);

        return RenderType.create("abyss_energy", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false,
                true, compositeState);
    });

    public static final Function<ResourceLocation, RenderType> ABYSS_LASER = Util.memoize(texture -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new ShaderStateShard(ProjectKCoreShaders::getAbyssLaserShader))
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST).setLayeringState(VIEW_OFFSET_Z_LAYERING).setCullState(NO_CULL)
                .createCompositeState(false);

        return RenderType.create("abyss_laser", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false,
                true, compositeState);
    });

    public static final Function<ResourceLocation, RenderType> ABYSS_BURST = Util.memoize(texture -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new ShaderStateShard(ProjectKCoreShaders::getAbyssBurstShader))
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);

        return RenderType.create("abyss_burst", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536,
                false, true, compositeState);
    });

    public ProjectKRenderTypes(String string, Runnable runnable, Runnable runnable2) {
        super(string, runnable, runnable2);
    }
}
