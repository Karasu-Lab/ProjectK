package com.karasu256.projectk.api.energy;

import com.karasu256.projectk.energy.AbyssEnergyColor;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.resources.ResourceLocation;

public enum PKMaterials {
    ABYSS("abyss", 0xB432FF, "Abyss", "深淵", ProjectKEnergies.EnergyKind.NEUTRAL), YIN("yin_abyss", 0x5000FF, "Yin",
            "陰", ProjectKEnergies.EnergyKind.YIN), YANG("yang_abyss", 0xFF50FF, "Yang", "陽",
            ProjectKEnergies.EnergyKind.YANG), GOD("god", 0xFFD700, "God", "神",
            ProjectKEnergies.EnergyKind.NEUTRAL), EXTREME("extreme", AbyssEnergyColor.EXTREME.getRGB(), "Extreme", "極",
            ProjectKEnergies.EnergyKind.NEUTRAL), EVIL("evil", AbyssEnergyColor.EVIL.getRGB(), "Evil", "悪",
            ProjectKEnergies.EnergyKind.NEUTRAL), HEAT("heat", AbyssEnergyColor.HEAT.getRGB(), "Heat", "熱",
            ProjectKEnergies.EnergyKind.NEUTRAL), COLD("cold", AbyssEnergyColor.COLD.getRGB(), "Cold", "冷",
            ProjectKEnergies.EnergyKind.NEUTRAL), LOVE("love", AbyssEnergyColor.LOVE.getRGB(), "Love", "愛",
            ProjectKEnergies.EnergyKind.NEUTRAL);

    private final String id;
    private final int color;
    private final String enName;
    private final String jaName;
    private final ProjectKEnergies.EnergyKind kind;

    PKMaterials(String id, int color, String enName, String jaName, ProjectKEnergies.EnergyKind kind) {
        this.id = id;
        this.color = color;
        this.enName = enName;
        this.jaName = jaName;
        this.kind = kind;
    }

    public String id() {
        return id;
    }

    public int color() {
        return color;
    }

    public String enName() {
        return enName;
    }

    public String jaName() {
        return jaName;
    }

    public ProjectKEnergies.EnergyKind kind() {
        return kind;
    }

    public String energyIdPath() {
        return id + "_energy";
    }

    public static PKMaterials getByEnergyId(ResourceLocation energyId) {
        if (energyId == null)
            return ABYSS;
        String path = energyId.getPath();
        for (PKMaterials material : values()) {
            if (material.energyIdPath().equals(path)) {
                return material;
            }
        }
        return ABYSS;
    }
}
