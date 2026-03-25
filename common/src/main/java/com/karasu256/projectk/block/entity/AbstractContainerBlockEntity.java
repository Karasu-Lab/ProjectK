package com.karasu256.projectk.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractContainerBlockEntity extends AbstractAnimatableBlockEntity {
    protected ItemStack heldItem = ItemStack.EMPTY;
    protected static final String ITEM_TAG = "HeldItem";

    public AbstractContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(ItemStack stack) {
        this.heldItem = stack;
        setChanged();
        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (!heldItem.isEmpty()) {
            nbt.put(ITEM_TAG, heldItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains(ITEM_TAG)) {
            heldItem = ItemStack.parse(registries, nbt.getCompound(ITEM_TAG)).orElse(ItemStack.EMPTY);
        } else {
            heldItem = ItemStack.EMPTY;
        }
    }
}
