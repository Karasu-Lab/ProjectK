package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.data.TierUpgradeData;
import com.karasu256.projectk.energy.ITierInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class TierUpgradeItem extends ProjectKItem {
    public TierUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (context.getPlayer() == null || !context.getPlayer().isCrouching()) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        TierUpgradeData data = stack.get(ProjectKDataComponets.TIER_UPGRADE_DATA_COMPONENT_TYPE.get());
        if (data == null || data.amount() <= 0) {
            return InteractionResult.FAIL;
        }
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ITierInfo tierInfo)) {
            return InteractionResult.FAIL;
        }
        boolean upgraded = false;
        for (int i = 0; i < data.amount(); i++) {
            if (tierInfo.upgradeTier()) {
                upgraded = true;
            } else {
                break;
            }
        }
        if (!upgraded) {
            return InteractionResult.FAIL;
        }
        stack.shrink(1);
        be.setChanged();
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        return InteractionResult.SUCCESS;
    }
}
