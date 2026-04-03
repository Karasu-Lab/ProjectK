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

public class AbyssEnchanterTierManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger(ProjectK.MOD_ID);
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "abyss_enchant_tiers";
    private static final List<AbyssEnchanterTier> TIERS = new ArrayList<>();

    public AbyssEnchanterTierManager() {
        super(GSON, DIRECTORY);
    }

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AbyssEnchanterTierManager());
    }

    public static List<AbyssEnchanterTier> getTiers() {
        return List.copyOf(TIERS);
    }

    public static AbyssEnchanterTier getBestTier(long energy) {
        AbyssEnchanterTier best = null;
        for (AbyssEnchanterTier tier : TIERS) {
            if (tier.cost() <= energy) {
                best = tier;
            }
        }
        return best;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<AbyssEnchanterTier> next = new ArrayList<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            AbyssEnchanterTier.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, entry.getValue())
                    .resultOrPartial(error -> LOGGER.error("Failed to parse abyss enchanter tier {}: {}", entry.getKey(), error))
                    .ifPresent(next::add);
        }
        if (next.isEmpty()) {
            next.add(new AbyssEnchanterTier(10, 10000L));
            next.add(new AbyssEnchanterTier(20, 20000L));
            next.add(new AbyssEnchanterTier(30, 30000L));
        }
        next.sort(Comparator.comparingInt(AbyssEnchanterTier::level));
        TIERS.clear();
        TIERS.addAll(next);
        LOGGER.info("Loaded {} abyss enchanter tiers", TIERS.size());
    }
}
