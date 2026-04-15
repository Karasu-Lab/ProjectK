package com.karasu256.projectk.api.datagen.impl;

import net.minecraft.data.PackOutput;

import java.util.List;

public abstract class AbstractListDataProvider<T> extends AbstractProjectKDataProvider<List<T>> {
    public AbstractListDataProvider(PackOutput output, String name) {
        super(output, name);
    }
}
