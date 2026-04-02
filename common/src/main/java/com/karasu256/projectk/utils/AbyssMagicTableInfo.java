package com.karasu256.projectk.utils;

public final class AbyssMagicTableInfo {
    private AbyssMagicTableInfo() {
    }

    public static int progressWidth(int progress, int maxProgress, int maxWidth) {
        int safeMax = Math.max(maxProgress, 1);
        return (int) (maxWidth * (progress / (float) safeMax));
    }

    public static int progressPercent(int progress, int maxProgress) {
        int safeMax = Math.max(maxProgress, 1);
        return Math.round(progress * 100.0f / safeMax);
    }

    public static float progressRatio(int progress, int maxProgress) {
        int safeMax = Math.max(maxProgress, 1);
        return Math.min(1.0f, Math.max(0.0f, progress / (float) safeMax));
    }
}
