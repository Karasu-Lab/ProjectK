package com.karasu256.projectk.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AbyssLaserEmitterTierManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger(ProjectK.MOD_ID);
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "abyss_laser_emitter_tiers";
    private static final List<AbyssLaserEmitterTier> TIERS = new ArrayList<>();

    public AbyssLaserEmitterTierManager() {
        super(GSON, DIRECTORY);
    }

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AbyssLaserEmitterTierManager());
    }

    public static List<AbyssLaserEmitterTier> getTiers() {
        return List.copyOf(TIERS);
    }

    public static AbyssLaserEmitterTier getTier(int level) {
        for (AbyssLaserEmitterTier tier : TIERS) {
            if (tier.level() == level) {
                return tier;
            }
        }
        return TIERS.isEmpty() ? new AbyssLaserEmitterTier(0, 30000L, 100L, 100, 20) : TIERS.get(0);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<AbyssLaserEmitterTier> next = new ArrayList<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            AbyssLaserEmitterTier.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, entry.getValue())
                    .resultOrPartial(error -> LOGGER.error("Failed to parse abyss laser emitter tier {}: {}", entry.getKey(), error))
                    .ifPresent(next::add);
        }
        if (next.isEmpty()) {
            next.add(new AbyssLaserEmitterTier(0, 30000L, 100L, 100, 20));
            next.add(new AbyssLaserEmitterTier(1, 60000L, 200L, 200, 15));
            next.add(new AbyssLaserEmitterTier(2, 120000L, 400L, 400, 10));
        }
        next.sort(Comparator.comparingInt(AbyssLaserEmitterTier::level));
        TIERS.clear();
        TIERS.addAll(next);
        LOGGER.info("Loaded {} abyss laser emitter tiers", TIERS.size());
    }
}
