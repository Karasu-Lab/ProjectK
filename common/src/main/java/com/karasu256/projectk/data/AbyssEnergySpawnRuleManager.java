package com.karasu256.projectk.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.data.spawn.BlockCondition;
import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.mojang.serialization.JsonOps;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AbyssEnergySpawnRuleManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger(ProjectK.MOD_ID);
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "abyss_energy_spawns";
    private static final List<AbyssEnergySpawnRule> RULES = new ArrayList<>();

    public AbyssEnergySpawnRuleManager() {
        super(GSON, DIRECTORY);
    }

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AbyssEnergySpawnRuleManager());
    }

    public static List<AbyssEnergySpawnRule> getRules() {
        return List.copyOf(RULES);
    }

    public static Optional<AbyssEnergySpawnRule> findMatch(Level level, BlockPos pos, LivingEntity entity) {
        for (AbyssEnergySpawnRule rule : RULES) {
            if (!rule.mob().matches(entity)) {
                continue;
            }
            BlockCondition block = rule.block();
            if (!block.matches(level, pos)) {
                continue;
            }
            return Optional.of(rule);
        }
        return Optional.empty();
    }

    public static ResourceLocation fallbackEnergyId() {
        return ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL);
    }

    public static long fallbackAmount(LivingEntity entity) {
        return IAbyssEnergy.calculateEnergy(entity);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<AbyssEnergySpawnRule> next = new ArrayList<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            AbyssEnergySpawnRule.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).resultOrPartial(error ->
                            LOGGER.error("Failed to parse abyss energy spawn rule {}: {}", entry.getKey(), error))
                    .ifPresent(next::add);
        }
        next.sort(Comparator.comparing(rule -> rule.energyId().toString()));
        RULES.clear();
        RULES.addAll(next);
        LOGGER.info("Loaded {} abyss energy spawn rules", RULES.size());
    }
}
