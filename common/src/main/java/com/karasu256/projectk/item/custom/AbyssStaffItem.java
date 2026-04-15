package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.AbyssEnergyUtils;
import com.karasu256.projectk.entity.AbyssBurstEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class AbyssStaffItem extends ProjectKItem {
    public static final long MAX_CAPACITY = 5000L;
    private final int minChargeTicks;
    private final int maxChargeTicks;
    private final long minEnergy;
    private final long maxEnergy;

    public AbyssStaffItem(ProjectKItem.Properties properties, int minChargeTicks, int maxChargeTicks, long minEnergy, long maxEnergy) {
        super(properties);
        this.minChargeTicks = minChargeTicks;
        this.maxChargeTicks = maxChargeTicks;
        this.minEnergy = minEnergy;
        this.maxEnergy = maxEnergy;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCreative() || getEnergyAmount(stack) >= minEnergy) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player))
            return;

        int useDuration = this.getUseDuration(stack, entity) - timeLeft;
        float progress = Math.min(1.0f, (float) (useDuration - minChargeTicks) / (maxChargeTicks - minChargeTicks));
        if (useDuration < minChargeTicks)
            progress = 0;

        long energyToConsume = minEnergy + (long) (progress * (maxEnergy - minEnergy));
        long currentEnergy = getEnergyAmount(stack);

        boolean isCreative = player.isCreative();
        if (isCreative || currentEnergy >= energyToConsume) {
            if (!level.isClientSide) {
                AbyssBurstEntity burst = new AbyssBurstEntity(level, player, energyToConsume,
                        AbyssEnergyUtils.getEffectiveEnergyId(stack), progress);
                burst.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(burst);

                if (!isCreative) {
                    consumeEnergy(stack, energyToConsume);
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player))
            return;

        long current = getEnergyAmount(stack);
        if (current < MAX_CAPACITY) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() instanceof AbyssBraceletItem) {
                    long braceletEnergy = getEnergyAmount(invStack);
                    if (braceletEnergy > 0) {
                        long transfer = Math.min(50, Math.min(MAX_CAPACITY - current, braceletEnergy));
                        if (transfer > 0) {
                            consumeEnergy(invStack, transfer);
                            addEnergy(stack, transfer);
                            current += transfer;
                        }
                    }
                }
                if (current >= MAX_CAPACITY)
                    break;
            }
        }
    }

    private long getEnergyAmount(ItemStack stack) {
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        return data == null ? 0L : data.amountOrZero();
    }

    private void consumeEnergy(ItemStack stack, long amount) {
        long current = getEnergyAmount(stack);
        AbyssEnergyData.applyToStack(stack, AbyssEnergyUtils.getEffectiveEnergyId(stack),
                Math.max(0, current - amount));
    }

    private void addEnergy(ItemStack stack, long amount) {
        long current = getEnergyAmount(stack);
        AbyssEnergyData.applyToStack(stack, AbyssEnergyUtils.getEffectiveEnergyId(stack),
                Math.min(MAX_CAPACITY, current + amount));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.min(13L, Math.round((getEnergyAmount(stack) / (double) MAX_CAPACITY) * 13.0));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return AbyssEnergyUtils.getEnergyColor(stack);
    }
}
