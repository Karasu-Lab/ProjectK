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
    }

    public static ShaderInstance getAbyssEnergyShader() {
        return abyssEnergyShader;
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}
