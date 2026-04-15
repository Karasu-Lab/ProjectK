package com.karasu256.projectk.utils;

public final class MathUtils {
    public static float percentTo(float percent, float max) {
        float clamped = Math.max(0.0f, Math.min(1.0f, percent));
        return Math.round(clamped * 16.0f);
    }
}
