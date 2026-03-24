package com.karasu256.projectk.event;

public class ModEvents {
    public static void init() {
        ProjectKEntityEvents.LIVING_DEATH_AROUND_ANY_BLOCK.register((entity, pos, level) -> null);
    }
}
