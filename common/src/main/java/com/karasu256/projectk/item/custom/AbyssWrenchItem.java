package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.block.custom.AbyssEnergyCable;
import com.karasu256.projectk.block.custom.AbyssEnergyCable.ConnectionMode;
import com.karasu256.projectk.block.entity.AbyssEnergyCableBlockEntity;
import com.karasu256.projectk.data.AbyssWrenchBehaviorData;
import com.karasu256.projectk.data.AbyssWrenchBehaviorData.AbyssWrenchBehavior;
import com.karasu256.projectk.energy.ITierInfo;
import com.karasu256.projectk.item.ProjectKItems;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbyssWrenchItem extends ProjectKItem {
    public AbyssWrenchItem(Properties properties) {
        super(properties);
    }

    private static Direction selectTargetDirection(UseOnContext context) {
        Vec3 hit = context.getClickLocation();
        BlockPos pos = context.getClickedPos();
        double localX = hit.x - pos.getX() - 0.5;
        double localY = hit.y - pos.getY() - 0.5;
        double localZ = hit.z - pos.getZ() - 0.5;
        double absX = Math.abs(localX);
        double absY = Math.abs(localY);
        double absZ = Math.abs(localZ);
        if (absX >= absY && absX >= absZ) {
            return localX >= 0 ? Direction.EAST : Direction.WEST;
        }
        if (absY >= absX && absY >= absZ) {
            return localY >= 0 ? Direction.UP : Direction.DOWN;
        }
        return localZ >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static ConnectionMode mapBehavior(AbyssWrenchBehavior behavior) {
        return switch (behavior) {
            case NORMAL -> ConnectionMode.CONNECTED;
            case INPUT -> ConnectionMode.INPUT;
            case OUTPUT -> ConnectionMode.OUTPUT;
            case NONE, DOWNGRADE -> ConnectionMode.NONE;
        };
    }

    private static AbyssWrenchBehavior mapModeToBehavior(ConnectionMode mode) {
        return switch (mode) {
            case CONNECTED -> AbyssWrenchBehavior.NORMAL;
            case INPUT -> AbyssWrenchBehavior.INPUT;
            case OUTPUT -> AbyssWrenchBehavior.OUTPUT;
            case NONE -> AbyssWrenchBehavior.NONE;
        };
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
        AbyssWrenchBehavior behavior = AbyssWrenchBehaviorData.getBehavior(stack);

        if (context.getPlayer() != null && context.getPlayer().isCrouching()) {
            if (behavior == AbyssWrenchBehavior.DOWNGRADE && be instanceof ITierInfo tierInfo) {
                int currentTier = tierInfo.getTier();
                if (currentTier > 1) {
                    tierInfo.setTier(currentTier - 1);
                    Block.popResource(context.getLevel(), context.getClickedPos(),
                            new ItemStack(ProjectKItems.TIER_UPGRADE.get()));
                    be.setChanged();
                    context.getLevel().sendBlockUpdated(context.getClickedPos(),
                            context.getLevel().getBlockState(context.getClickedPos()),
                            context.getLevel().getBlockState(context.getClickedPos()), 3);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }
            AbyssWrenchBehaviorData.cycleBehavior(stack);
            AbyssWrenchBehavior next = AbyssWrenchBehaviorData.getBehavior(stack);
            context.getPlayer().displayClientMessage(Component.translatable("tooltip.projectk.abyss_wrench_behavior",
                    Component.translatable(next.translationKey())), true);
            return InteractionResult.SUCCESS;
        }

        if (be instanceof AbyssEnergyCableBlockEntity cable) {
            Direction target = selectTargetDirection(context);
            BlockPos pos = context.getClickedPos();
            ConnectionMode nextMode;
            if (context.getPlayer() != null && context.getPlayer().isCrouching()) {
                ConnectionMode current = cable.getModeForSide(target);
                nextMode = current.next();
            } else {
                nextMode = mapBehavior(behavior);
            }
            if (!AbyssEnergyCable.canConnect(context.getLevel(), pos.relative(target))) {
                nextMode = ConnectionMode.NONE;
            }
            cable.setModeForSide(target, nextMode);
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(
                        Component.translatable("tooltip.projectk.abyss_wrench_behavior",
                                Component.translatable(mapModeToBehavior(nextMode).translationKey())), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (be instanceof ICableInputable || be instanceof ICableOutputable) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isCrouching()) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide) {
            AbyssWrenchBehaviorData.cycleBehavior(stack);
            AbyssWrenchBehavior next = AbyssWrenchBehaviorData.getBehavior(stack);
            player.displayClientMessage(Component.translatable("tooltip.projectk.abyss_wrench_behavior",
                    Component.translatable(next.translationKey())), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        AbyssWrenchBehavior behavior = AbyssWrenchBehaviorData.getBehavior(stack);
        tooltip.add(Component.translatable("tooltip.projectk.abyss_wrench_behavior",
                Component.translatable(behavior.translationKey())));
    }
}
