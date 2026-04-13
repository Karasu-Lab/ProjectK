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
import java.util.Locale;

public record MobCondition(List<MobCategory> types, List<TagKey<EntityType<?>>> tags, List<ResourceLocation> ids,
                           List<String> groups) {
    public MobCondition() {
        this(List.of(), List.of(), List.of(), List.of());
    }

    public static final Codec<MobCategory> MOB_CATEGORY_CODEC = Codec.STRING.comapFlatMap(MobCondition::parseMobCategory, MobCategory::getName);

    public static final Codec<MobCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.list(MOB_CATEGORY_CODEC).optionalFieldOf("types", List.of()).forGetter(MobCondition::types), Codec.list(TagKey.codec(Registries.ENTITY_TYPE)).optionalFieldOf("tags", List.of()).forGetter(MobCondition::tags), Codec.list(ResourceLocation.CODEC).optionalFieldOf("ids", List.of()).forGetter(MobCondition::ids), Codec.list(Codec.STRING).optionalFieldOf("groups", List.of()).forGetter(MobCondition::groups)).apply(instance, MobCondition::new));

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
        boolean hasCriteria = !types.isEmpty() || !tags.isEmpty() || !ids.isEmpty() || !groups.isEmpty();
        if (!hasCriteria) {
            return true;
        }
        if (!groups.isEmpty() && matchesGroup(entity.getType().getCategory())) {
            return true;
        }
        if (!types.isEmpty() && types.contains(entity.getType().getCategory())) {
            return true;
        }
        if (!tags.isEmpty()) {
            for (TagKey<EntityType<?>> tag : tags) {
                if (entity.getType().is(tag)) {
                    return true;
                }
            }
        }
        if (!ids.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            return ids.contains(id);
        }
        return false;
    }

    private boolean matchesGroup(MobCategory category) {
        for (String group : groups) {
            String key = group == null ? "" : group.toLowerCase(Locale.ROOT);
            if ("friendly".equals(key) && isFriendlyCategory(category)) {
                return true;
            }
            if ("hostile".equals(key) && category == MobCategory.MONSTER) {
                return true;
            }
            if (("other".equals(key) || "misc".equals(key)) && isOtherCategory(category)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFriendlyCategory(MobCategory category) {
        return category == MobCategory.CREATURE || category == MobCategory.WATER_CREATURE || category == MobCategory.AXOLOTLS || category == MobCategory.UNDERGROUND_WATER_CREATURE;
    }

    private boolean isOtherCategory(MobCategory category) {
        return category == MobCategory.AMBIENT || category == MobCategory.WATER_AMBIENT || category == MobCategory.MISC;
    }
}
