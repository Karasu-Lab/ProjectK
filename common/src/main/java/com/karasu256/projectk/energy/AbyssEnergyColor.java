package com.karasu256.projectk.energy;

import java.awt.*;

public class AbyssEnergyColor {
    public static final Color PURPLE = new Color(180, 50, 255);
    public static final Color BLUE = new Color(50, 160, 255);
    public static final Color GOLD = new Color(255, 215, 0);
    public static final Color RED = new Color(255, 50, 50);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color EXTREME = new Color(0, 0, 0);
    public static final Color EVIL = new Color(128, 0, 255);
    public static final Color HEAT = new Color(255, 0, 0);
    public static final Color COLD = new Color(0, 255, 255);
    public static final Color LOVE = new Color(255, 0, 255);

    public static Color getColor(long energy) {
        if (energy >= 1_000_000_000_000_000L) {
            return WHITE;
        } else if (energy >= 1_000_000_000_000L) {
            return GOLD;
        } else if (energy >= 1_000_000L) {
            return BLUE;
        } else if (energy >= 1_000L) {
            return RED;
        } else {
            return PURPLE;
        }
    }
}
