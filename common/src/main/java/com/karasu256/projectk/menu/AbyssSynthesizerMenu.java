package com.karasu256.projectk.menu;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.AbyssSynthesizerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AbyssSynthesizerMenu extends AbstractContainerMenu {
    private static final int CENTER_X = 88;
    private static final int CENTER_Y = 78;
    private static final float SLOT_RADIUS = 38.0f;

    private final Container container;
    private final ContainerLevelAccess access;
    private final BlockEntity blockEntity;

    public AbyssSynthesizerMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(7), ContainerLevelAccess.NULL, null);
    }

    public AbyssSynthesizerMenu(int syncId, Inventory playerInventory, BlockEntity blockEntity) {
        this(syncId, playerInventory, ((Container) blockEntity),
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), blockEntity);
    }

    public AbyssSynthesizerMenu(int syncId, Inventory playerInventory, Container container, ContainerLevelAccess access, BlockEntity blockEntity) {
        super(ProjectKMenus.ABYSS_SYNTHESIZER.get(), syncId);
        checkContainerSize(container, 7);
        this.container = container;
        this.access = access;
        this.blockEntity = blockEntity;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, 0, CENTER_X - 8, CENTER_Y - 8) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(-90 + 60 * i);
            int posX = CENTER_X + (int) Math.round(Math.cos(angle) * SLOT_RADIUS);
            int posY = CENTER_Y + (int) Math.round(Math.sin(angle) * SLOT_RADIUS);
            this.addSlot(new Slot(container, i + 1, posX - 8, posY - 8));
        }

        int invX = 8;
        int invY = 158;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, invX + col * 18, invY + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, invX + col * 18, invY + 58));
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public AbyssSynthesizerBlockEntity getSynthesizer() {
        if (blockEntity instanceof AbyssSynthesizerBlockEntity be) {
            return be;
        }
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < 7) {
                if (!this.moveItemStackTo(itemStack2, 7, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 1, 7, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ProjectKBlocks.ABYSS_SYNTHESIZER.get());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            AbyssSynthesizerBlockEntity be = getSynthesizer();
            if (be != null && be.getLevel() != null && !be.getLevel().isClientSide) {
                be.dumpEnergy();
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
