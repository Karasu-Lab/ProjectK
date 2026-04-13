package com.karasu256.projectk.api.datagen.impl;

import com.google.gson.JsonElement;
import com.karasu256.projectk.utils.Id;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class AbstractProjectKDataProvider<T> implements DataProvider {
    private CachedOutput cache;
    private final PackOutput.PathProvider pathProvider;
    private final List<CompletableFuture<?>> futures;
    private final String name;

    public AbstractProjectKDataProvider(PackOutput output, String name) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, name);
        this.futures = new ArrayList<>();
        this.name = name;
    }

    protected void createCache(CachedOutput output) {
        this.cache = output;
    }

    public void addData(String id, T data) {
        write(cache, id, data);
    }

    public CompletableFuture<?> runDataGen(CachedOutput cachedOutput) {
        return getCachedFutures();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        createCache(cachedOutput);
        return runDataGen(cachedOutput);
    }

    @NotNull
    private CompletableFuture<?> getCachedFutures() {
        var allResults = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        return CompletableFuture.allOf(allResults);
    }

    protected CompletableFuture<?> write(CachedOutput output, String id, T data) {
        JsonElement json = getElement(data);
        Path path = pathProvider.json(Id.id(id));
        var future = DataProvider.saveStable(output, json, path);
        futures.add(future);
        return future;
    }

    public abstract JsonElement getElement(T data);

    @Override
    public String getName() {
        return name;
    }
}
