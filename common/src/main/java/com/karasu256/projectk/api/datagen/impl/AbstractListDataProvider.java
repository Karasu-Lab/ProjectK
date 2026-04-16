package com.karasu256.projectk.api.datagen.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;

import java.util.List;

public abstract class AbstractListDataProvider<T> extends AbstractProjectKDataProvider<List<T>> {
    public AbstractListDataProvider(PackOutput output, String name) {
        super(output, name);
    }

    public AbstractListDataProvider(PackOutput output, PackOutput.Target target, String name) {
        super(output, target, name);
    }
}
