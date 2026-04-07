package com.karasu256.projectk.api.energy;

import com.karasu256.projectk.energy.ProjectKEnergies;

public enum PKMaterials {
    ABYSS("abyss", 0xB432FF, "Abyss", "深淵", ProjectKEnergies.EnergyKind.NEUTRAL),
    YIN("yin_abyss", 0x5000FF, "Yin", "陰", ProjectKEnergies.EnergyKind.YIN),
    YANG("yang_abyss", 0xFF50FF, "Yang", "陽", ProjectKEnergies.EnergyKind.YANG);

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
}
