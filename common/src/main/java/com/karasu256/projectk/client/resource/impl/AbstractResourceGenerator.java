package com.karasu256.projectk.client.resource.impl;

import com.google.gson.JsonObject;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;

public abstract class AbstractResourceGenerator implements IResourceGenerator {
    public abstract void generate(ResourceManager manager, ResourceSink sink);

    protected final void tint(TextureImage img, int tint) {
        int targetR = (tint >> 16) & 0xFF;
        int targetG = (tint >> 8) & 0xFF;
        int targetB = tint & 0xFF;

        img.forEachPixel(pixel -> {
            int color = pixel.getValue();
            int alpha = FastColor.ABGR32.alpha(color);
            if (alpha > 0) {
                int r = (FastColor.ABGR32.red(color) * targetR) / 255;
                int g = (FastColor.ABGR32.green(color) * targetG) / 255;
                int b = (FastColor.ABGR32.blue(color) * targetB) / 255;
                pixel.setValue(FastColor.ABGR32.color(alpha, b, g, r));
            }
        });
    }

    protected final void brightenedAdditiveTint(TextureImage img, int tint) {
        int tintR = (tint >> 16) & 0xFF;
        int tintG = (tint >> 8) & 0xFF;
        int tintB = tint & 0xFF;

        img.forEachPixel(pixel -> {
            int color = pixel.getValue();
            int alpha = FastColor.ABGR32.alpha(color);
            if (alpha > 0) {
                int r = FastColor.ABGR32.red(color);
                int g = FastColor.ABGR32.green(color);
                int b = FastColor.ABGR32.blue(color);

                int gray = (int) ((r * 0.299f + g * 0.587f + b * 0.114f) * 1.5f);
                gray = Math.min(255, gray);

                int resR = Math.min(255, gray + tintR);
                int resG = Math.min(255, gray + tintG);
                int resB = Math.min(255, gray + tintB);

                pixel.setValue(FastColor.ABGR32.color(alpha, resB, resG, resR));
            }
        });
    }

    protected final JsonObject createGeneratedModel(ResourceLocation texture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texture.toString());
        model.add("textures", textures);
        return model;
    }
}
