package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.api.container.IHeldItem;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractContainerBlockEntity extends AbstractAnimatableBlockEntity implements IHeldItem {
    protected ItemStack heldItem = ItemStack.EMPTY;

    public AbstractContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public ItemStack getHeldItem() {
        return heldItem;
    }

    @Override
    public void setHeldItem(ItemStack stack) {
        this.heldItem = stack;
        setChanged();
        sync();
    }

    @Override
    public ResourceLocation getNbtId() {
        return Id.id("held_item");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (!heldItem.isEmpty()) {
            nbt.put(getNbtId().toString(), heldItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        String key = getNbtId().toString();
        if (nbt.contains(key)) {
            heldItem = ItemStack.parse(registries, nbt.getCompound(key)).orElse(ItemStack.EMPTY);
        } else {
            heldItem = ItemStack.EMPTY;
        }
    }
}
