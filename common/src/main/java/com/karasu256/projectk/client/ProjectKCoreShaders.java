package com.karasu256.projectk.client;

import com.karasu256.projectk.utils.Id;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ProjectKCoreShaders {
    private static ShaderInstance abyssEnergyShader;

    public static void init(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> registrations) {
        registrations.accept(Id.id("abyss_energy"), DefaultVertexFormat.NEW_ENTITY, inst -> abyssEnergyShader = inst);
        registrations.accept(Id.id("abyss_laser"), DefaultVertexFormat.NEW_ENTITY, inst -> abyssLaserShader = inst);
        registrations.accept(Id.id("abyss_burst"), DefaultVertexFormat.POSITION_TEX_COLOR, inst -> abyssBurstShader = inst);
    }

    private static ShaderInstance abyssLaserShader;

    public static ShaderInstance getAbyssLaserShader() {
        return abyssLaserShader;
    }

    public static ShaderInstance getAbyssEnergyShader() {
        return abyssEnergyShader;
    }

    private static ShaderInstance abyssBurstShader;

    public static ShaderInstance getAbyssBurstShader() {
        return abyssBurstShader;
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}
