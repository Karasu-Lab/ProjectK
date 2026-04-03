package com.karasu256.projectk.menu;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.AbyssEnchanterBlockEntity;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.registry.ProjectKTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AbyssEnchanterMenu extends AbstractContainerMenu {
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int CONTAINER_SIZE = 2;
    private static final int DATA_SIZE = 17;

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final AbyssEnchanterBlockEntity blockEntity;

    public AbyssEnchanterMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_SIZE), ContainerLevelAccess.NULL, null);
    }

    public AbyssEnchanterMenu(int syncId, Inventory inventory, AbyssEnchanterBlockEntity blockEntity) {
        this(syncId, inventory, new EnchanterContainer(blockEntity), new EnchanterData(blockEntity), ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), blockEntity);
    }

    private AbyssEnchanterMenu(int syncId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access, AbyssEnchanterBlockEntity blockEntity) {
        super(ProjectKMenus.ABYSS_ENCHANTER.get(), syncId);
        this.container = container;
        this.data = data;
        this.access = access;
        this.blockEntity = blockEntity;

        addSlot(new EnchanterInputSlot(container, SLOT_INPUT, 15, 47));
        addSlot(new ResultSlot(container, SLOT_OUTPUT, 35, 47));

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ProjectKBlocks.ABYSS_ENCHANTER.get());
    }

    public int getTierLevel(int index) {
        return data.get(index);
    }

    public int getTierCost(int index) {
        return data.get(3 + index);
    }

    public long getEnergy() {
        int low = data.get(6);
        int high = data.get(7);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    public long getEnergyCapacity() {
        int low = data.get(8);
        int high = data.get(9);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    public ResourceLocation getEnergyId() {
        return ProjectKEnergies.getEnergyIdByModelIndex(data.get(10));
    }

    public int getOptionEnchantmentId(int index) {
        return data.get(11 + index);
    }

    public int getOptionEnchantmentLevel(int index) {
        return data.get(14 + index);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (blockEntity == null) {
            return false;
        }
        return blockEntity.applyEnchantment(id, player);
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

    private static class EnchanterInputSlot extends Slot {
        public EnchanterInputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ProjectKTags.Items.BOOKS)
                    || stack.is(ItemTags.AXES)
                    || stack.is(ItemTags.HOES)
                    || stack.is(ItemTags.PICKAXES)
                    || stack.is(ItemTags.SHOVELS)
                    || stack.is(ItemTags.SWORDS)
                    || stack.is(ItemTags.TRIMMABLE_ARMOR)
                    || stack.is(Items.MACE)
                    || stack.is(Items.TRIDENT);
        }
    }

    private static class EnchanterContainer extends SimpleContainer {
        private final AbyssEnchanterBlockEntity blockEntity;

        public EnchanterContainer(AbyssEnchanterBlockEntity blockEntity) {
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

    private record EnchanterData(AbyssEnchanterBlockEntity blockEntity) implements ContainerData {
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
