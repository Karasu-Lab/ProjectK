package com.karasu256.projectk.client.resource.block;

import com.karasu256.projectk.client.resource.impl.AbstractPolishedBlockGenerator;
import com.karasu256.projectk.utils.Id;
import net.minecraft.resources.ResourceLocation;

public class PolishedNetherrackGenerator extends AbstractPolishedBlockGenerator {
    public PolishedNetherrackGenerator() {
        super(ResourceLocation.withDefaultNamespace("block/netherrack"), Id.id("block/polished_netherrack"));
    }
}
