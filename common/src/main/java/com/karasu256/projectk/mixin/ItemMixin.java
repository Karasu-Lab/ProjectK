package com.karasu256.projectk.mixin;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.item.custom.AbyssEnergyItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void projectk$appendAbyssEnergyTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        List<AbyssEnergyData> entries = readEnergyList(stack);
        if (entries.isEmpty()) {
            return;
        }
        EnergyCapacityData data = stack.get(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get());

        for (AbyssEnergyData energy : entries) {
            if (energy == null || energy.energyId() == null) {
                continue;
            }
            tooltip.add(AbyssEnergyItem.buildTooltip(energy.energyId(), energy.amountOrZero(), data));
        }
    }

    private List<AbyssEnergyData> readEnergyList(ItemStack stack) {
        List<AbyssEnergyData> list = new ArrayList<>();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String key = EnergyKeys.ENERGY_LIST.toString();
        if (tag.contains(key, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(key, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                AbyssEnergyData.CODEC.parse(NbtOps.INSTANCE, listTag.getCompound(i)).result().ifPresent(list::add);
            }
        }
        if (list.isEmpty()) {
            AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            if (data != null && data.energyId() != null) {
                list.add(data);
            }
        }
        return list;
    }
}
