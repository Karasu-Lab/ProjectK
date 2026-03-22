package com.karasu256.projectk.utils;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class Id {
    @NotNull
    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }
}
