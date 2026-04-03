package com.karasu256.projectk.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public record AbyssWrenchBehaviorData(AbyssWrenchBehavior behavior) {
    public static final Codec<AbyssWrenchBehaviorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbyssWrenchBehavior.CODEC.fieldOf("behavior").forGetter(AbyssWrenchBehaviorData::behavior)
    ).apply(instance, AbyssWrenchBehaviorData::new));

    public static final StreamCodec<ByteBuf, AbyssWrenchBehaviorData> STREAM_CODEC = StreamCodec.composite(
            AbyssWrenchBehavior.STREAM_CODEC,
            AbyssWrenchBehaviorData::behavior,
            AbyssWrenchBehaviorData::new
    );

    public static AbyssWrenchBehavior getBehavior(ItemStack stack) {
        AbyssWrenchBehaviorData data = stack.get(ProjectKDataComponets.ABYSS_WRENCH_BEHAVIOR_DATA_COMPONENT_TYPE.get());
        return data == null ? AbyssWrenchBehavior.NORMAL : data.behavior();
    }

    public static void setBehavior(ItemStack stack, AbyssWrenchBehavior behavior) {
        if (stack == null || behavior == null) {
            return;
        }
        stack.set(ProjectKDataComponets.ABYSS_WRENCH_BEHAVIOR_DATA_COMPONENT_TYPE.get(), new AbyssWrenchBehaviorData(behavior));
    }

    public static void cycleBehavior(ItemStack stack) {
        if (stack == null) {
            return;
        }
        AbyssWrenchBehavior current = getBehavior(stack);
        setBehavior(stack, current.next());
    }

    public enum AbyssWrenchBehavior implements StringRepresentable {
        NORMAL("normal"),
        INPUT("input"),
        OUTPUT("output"),
        NONE("none");

        public static final Codec<AbyssWrenchBehavior> CODEC = Codec.STRING.xmap(AbyssWrenchBehavior::fromString, AbyssWrenchBehavior::id);
        public static final StreamCodec<ByteBuf, AbyssWrenchBehavior> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(AbyssWrenchBehavior::fromOrdinal, AbyssWrenchBehavior::ordinal);

        private final String id;

        AbyssWrenchBehavior(String id) {
            this.id = id;
        }

        public static AbyssWrenchBehavior fromOrdinal(int ordinal) {
            AbyssWrenchBehavior[] values = values();
            if (ordinal < 0 || ordinal >= values.length) {
                return NORMAL;
            }
            return values[ordinal];
        }

        public static AbyssWrenchBehavior fromString(String value) {
            for (AbyssWrenchBehavior behavior : values()) {
                if (behavior.id.equals(value)) {
                    return behavior;
                }
            }
            return NORMAL;
        }

        public String id() {
            return id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }

        public String translationKey() {
            return "wrench_behavior.projectk." + id;
        }

        public AbyssWrenchBehavior next() {
            return switch (this) {
                case NORMAL -> INPUT;
                case INPUT -> OUTPUT;
                case OUTPUT -> NONE;
                case NONE -> NORMAL;
            };
        }
    }
}
