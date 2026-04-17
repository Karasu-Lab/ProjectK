package com.karasu256.projectk.client.resource.item;

import com.karasu256.projectk.client.resource.impl.AbstractResourceGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.awt.*;

public class DustResourceGenerator extends AbstractResourceGenerator {
    private final ResourceLocation input;
    private final ResourceLocation output;
    private final int color;

    public DustResourceGenerator(ResourceLocation input, ResourceLocation output, Color color) {
        this(input, output, color.getRGB());
    }

    public DustResourceGenerator(ResourceLocation input, ResourceLocation output, int color) {
        this.input = input;
        this.output = output;
        this.color = color;
    }

    @Override
    public void generate(ResourceManager manager, ResourceSink sink) {
        ResourceLocation baseId = ResourceLocation.withDefaultNamespace("item/redstone");

        try (TextureImage texture = TextureImage.open(manager, baseId)) {
            brightenedAdditiveTint(texture, color);
            sink.addTexture(input, texture);
            sink.addItemModel(output, createGeneratedModel(input));
        } catch (Exception ignored) {
        }
    }
}
