package com.karasu256.projectk.registry;

import net.minecraft.tags.TagKey;
import java.util.List;

public interface ProjectKProperties<T> {
    List<TagKey<T>> getTags();
}
