package com.karasu256.projectk.data.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;

import java.util.List;

public record MobCondition(List<MobCategory> types, List<TagKey<EntityType<?>>> tags, List<ResourceLocation> ids) {
    public static final Codec<MobCategory> MOB_CATEGORY_CODEC = Codec.STRING.comapFlatMap(
            MobCondition::parseMobCategory,
            MobCategory::getName
    );

    public static final Codec<MobCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(MOB_CATEGORY_CODEC).optionalFieldOf("types", List.of()).forGetter(MobCondition::types),
            Codec.list(TagKey.codec(Registries.ENTITY_TYPE)).optionalFieldOf("tags", List.of()).forGetter(MobCondition::tags),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("ids", List.of()).forGetter(MobCondition::ids)
    ).apply(instance, MobCondition::new));

    private static DataResult<MobCategory> parseMobCategory(String name) {
        String normalized = name.toLowerCase();
        for (MobCategory category : MobCategory.values()) {
            if (category.getName().equals(normalized)) {
                return DataResult.success(category);
            }
        }
        return DataResult.error(() -> "Unknown mob category: " + name);
    }

    public boolean matches(LivingEntity entity) {
        if (!types.isEmpty()) {
            return types.contains(entity.getType().getCategory());
        }
        if (!tags.isEmpty()) {
            for (TagKey<EntityType<?>> tag : tags) {
                if (entity.getType().is(tag)) {
                    return true;
                }
            }
            return false;
        }
        if (!ids.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            return ids.contains(id);
        }
        return true;
    }
}
