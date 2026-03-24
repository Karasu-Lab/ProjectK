package com.karasu256.projectk.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface ProjectKEntityEvents {
    Event<LivingEntityDeathAroundBlock> LIVING_DEATH_AROUND_ANY_BLOCK = EventFactory.createEventResult(LivingEntityDeathAroundBlock.class);
}
