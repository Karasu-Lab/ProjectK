package com.karasu256.projectk.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBlockProperties<SELF extends AbstractBlockProperties<SELF>> implements ProjectKProperties<Block> {
    protected final List<TagKey<Block>> tags;

    protected AbstractBlockProperties() {
        this(new ArrayList<>());
    }

    protected AbstractBlockProperties(List<TagKey<Block>> tags) {
        this.tags = List.copyOf(tags);
    }

    public abstract SELF withTags(List<TagKey<Block>> tags);

    public SELF tags(TagKey<Block> tag) {
        List<TagKey<Block>> newTags = new ArrayList<>(this.tags);
        newTags.add(tag);
        return withTags(newTags);
    }

    @Override
    public List<TagKey<Block>> getTags() {
        return tags;
    }
}
