package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.block.entity.AbyssEnergyCableBlockEntity;
import com.karasu256.projectk.data.AbyssWrenchBehaviorData;
import com.karasu256.projectk.data.AbyssWrenchBehaviorData.AbyssWrenchBehavior;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbyssWrenchItem extends ProjectKItem {
    public AbyssWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
        if (be instanceof AbyssEnergyCableBlockEntity cable) {
            if (context.getPlayer() != null && context.getPlayer().isCrouching()) {
                AbyssWrenchBehavior current = cable.getBehavior(context.getClickedFace());
                cable.setBehavior(context.getClickedFace(), current.next());
                return InteractionResult.SUCCESS;
            }
            AbyssWrenchBehavior behavior = AbyssWrenchBehaviorData.getBehavior(stack);
            cable.setBehavior(context.getClickedFace(), behavior);
            return InteractionResult.SUCCESS;
        }

        if (context.getPlayer() != null && context.getPlayer().isCrouching()) {
            AbyssWrenchBehaviorData.cycleBehavior(stack);
            return InteractionResult.SUCCESS;
        }

        if (be instanceof ICableInputable || be instanceof ICableOutputable) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        AbyssWrenchBehavior behavior = AbyssWrenchBehaviorData.getBehavior(stack);
        tooltip.add(Component.translatable("tooltip.projectk.abyss_wrench_behavior", Component.translatable(behavior.translationKey())));
    }

}
