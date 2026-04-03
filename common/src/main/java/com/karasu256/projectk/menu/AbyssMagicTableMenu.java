package com.karasu256.projectk.menu;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.AbyssMagicTableBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class AbyssMagicTableMenu extends AbstractContainerMenu {
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int CONTAINER_SIZE = 2;
    private static final int DATA_SIZE = 6;

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public AbyssMagicTableMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_SIZE), ContainerLevelAccess.NULL);
    }

    public AbyssMagicTableMenu(int syncId, Inventory inventory, AbyssMagicTableBlockEntity blockEntity) {
        this(syncId, inventory, new MagicTableContainer(blockEntity), new MagicTableData(blockEntity), ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()));
    }

    private AbyssMagicTableMenu(int syncId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(ProjectKMenus.ABYSS_MAGIC_TABLE.get(), syncId);
        this.container = container;
        this.data = data;
        this.access = access;

        addSlot(new Slot(container, SLOT_INPUT, 56, 35));
        addSlot(new ResultSlot(container, SLOT_OUTPUT, 116, 35));

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ProjectKBlocks.ABYSS_MAGIC_TABLE.get());
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public long getEnergy() {
        int low = data.get(2);
        int high = data.get(3);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    public long getEnergyCapacity() {
        int low = data.get(4);
        int high = data.get(5);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return stack;
        }
        ItemStack slotStack = slot.getItem();
        stack = slotStack.copy();

        if (index < CONTAINER_SIZE) {
            if (!moveItemStackTo(slotStack, CONTAINER_SIZE, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(slotStack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack;
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    private static class ResultSlot extends Slot {
        public ResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static class MagicTableContainer extends SimpleContainer {
        private final AbyssMagicTableBlockEntity blockEntity;

        public MagicTableContainer(AbyssMagicTableBlockEntity blockEntity) {
            super(CONTAINER_SIZE);
            this.blockEntity = blockEntity;
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot == SLOT_INPUT ? blockEntity.getInputItem() : blockEntity.getOutputItem();
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot == SLOT_INPUT) {
                blockEntity.setInputItem(stack);
            } else {
                blockEntity.setOutputItem(stack);
            }
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack current = getItem(slot);
            ItemStack result = current.split(amount);
            setItem(slot, current);
            return result;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }

    private record MagicTableData(AbyssMagicTableBlockEntity blockEntity) implements ContainerData {

        @Override
            public int get(int index) {
                return blockEntity.getDataValue(index);
            }

            @Override
            public void set(int index, int value) {
                blockEntity.setDataValue(index, value);
            }

            @Override
            public int getCount() {
                return DATA_SIZE;
            }
        }
}
