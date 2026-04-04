package com.karasu256.projectk.menu;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.AbyssChargerBlockEntity;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class AbyssChargerMenu extends AbstractContainerMenu {
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int CONTAINER_SIZE = 2;
    private static final int DATA_SIZE = 5;

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final AbyssChargerBlockEntity blockEntity;

    public AbyssChargerMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_SIZE), ContainerLevelAccess.NULL, null);
    }

    public AbyssChargerMenu(int syncId, Inventory inventory, AbyssChargerBlockEntity blockEntity) {
        this(syncId, inventory, new ChargerContainer(blockEntity), new ChargerData(blockEntity), ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), blockEntity);
    }

    private AbyssChargerMenu(int syncId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access, AbyssChargerBlockEntity blockEntity) {
        super(ProjectKMenus.ABYSS_CHARGER.get(), syncId);
        this.container = container;
        this.data = data;
        this.access = access;
        this.blockEntity = blockEntity;

        addSlot(new EnergyInputSlot(container, SLOT_INPUT, 80, 26, blockEntity));
        addSlot(new EnergyChargeSlot(container, SLOT_OUTPUT, 80, 58, blockEntity));

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ProjectKBlocks.ABYSS_CHARGER.get());
    }

    public long getEnergy() {
        int low = data.get(0);
        int high = data.get(1);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    public long getEnergyCapacity() {
        int low = data.get(2);
        int high = data.get(3);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    public ResourceLocation getEnergyId() {
        return ProjectKEnergies.getEnergyIdByModelIndex(data.get(4));
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
        } else {
            if (slots.get(SLOT_INPUT).mayPlace(slotStack)) {
                if (!moveItemStackTo(slotStack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, SLOT_OUTPUT, SLOT_OUTPUT + 1, false)) {
                return ItemStack.EMPTY;
            }
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

    private record ChargerContainer(AbyssChargerBlockEntity blockEntity) implements Container {
        @Override
        public int getContainerSize() {
            return CONTAINER_SIZE;
        }

        @Override
        public boolean isEmpty() {
            return blockEntity.getInputItem().isEmpty() && blockEntity.getOutputItem().isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot == SLOT_INPUT ? blockEntity.getInputItem() : blockEntity.getOutputItem();
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack current = getItem(slot);
            ItemStack result = current.split(amount);
            setItem(slot, current);
            return result;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack current = getItem(slot);
            setItem(slot, ItemStack.EMPTY);
            return current;
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
        public void setChanged() {
            blockEntity.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            blockEntity.setInputItem(ItemStack.EMPTY);
            blockEntity.setOutputItem(ItemStack.EMPTY);
        }
    }

    private record ChargerData(AbyssChargerBlockEntity blockEntity) implements ContainerData {
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
