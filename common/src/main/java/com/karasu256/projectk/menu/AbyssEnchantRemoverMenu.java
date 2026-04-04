package com.karasu256.projectk.menu;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.AbyssEnchantRemoverBlockEntity;
import com.karasu256.projectk.registry.ProjectKTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class AbyssEnchantRemoverMenu extends AbstractContainerMenu {
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_BOOK = 1;
    private static final int SLOT_OUTPUT = 2;
    private static final int CONTAINER_SIZE = 3;

    private final Container container;
    private final ContainerLevelAccess access;
    private final AbyssEnchantRemoverBlockEntity blockEntity;
    private final Inventory playerInventory;

    public AbyssEnchantRemoverMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), ContainerLevelAccess.NULL, null);
    }

    public AbyssEnchantRemoverMenu(int syncId, Inventory inventory, AbyssEnchantRemoverBlockEntity blockEntity) {
        this(syncId, inventory, new RemoverContainer(blockEntity), ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), blockEntity);
    }

    private AbyssEnchantRemoverMenu(int syncId, Inventory inventory, Container container, ContainerLevelAccess access, AbyssEnchantRemoverBlockEntity blockEntity) {
        super(ProjectKMenus.ABYSS_ENCHANT_REMOVER.get(), syncId);
        this.container = container;
        this.access = access;
        this.blockEntity = blockEntity;
        this.playerInventory = inventory;

        addSlot(new InputSlot(container, SLOT_INPUT, 56, 17, this));
        addSlot(new BookSlot(container, SLOT_BOOK, 56, 53, this));
        addSlot(new OutputSlot(container, SLOT_OUTPUT, 116, 35, this));

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ProjectKBlocks.ABYSS_ENCHANT_REMOVER.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return stack;
        }
        ItemStack slotStack = slot.getItem();
        stack = slotStack.copy();

        if (index < CONTAINER_SIZE) {
            if (!moveItemStackTo(slotStack, CONTAINER_SIZE, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            if (index == SLOT_OUTPUT && blockEntity != null) {
                blockEntity.onTakeOutput(player);
            }
        } else {
            if (canAcceptInput(slotStack, player)) {
                if (!moveItemStackTo(slotStack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotStack.is(ProjectKTags.Items.BOOKS)) {
                if (!moveItemStackTo(slotStack, SLOT_BOOK, SLOT_BOOK + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

    private boolean canAcceptInput(ItemStack stack, Player player) {
        if (blockEntity != null) {
            return blockEntity.canAcceptInput(stack);
        }
        if (stack.isEmpty()) {
            return false;
        }
        ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        return hasTaggedEnchant(player, enchants) || hasTaggedEnchant(player, stored);
    }

    private boolean hasTaggedEnchant(Player player, ItemEnchantments enchants) {
        if (enchants.isEmpty()) {
            return false;
        }
        var lookup = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        for (Holder<Enchantment> holder : enchants.keySet()) {
            if (holder.is(ProjectKTags.Enchantments.ABYSS_ENCHANT) && enchants.getLevel(holder) > 0) {
                return true;
            }
        }
        return false;
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

    private static class InputSlot extends Slot {
        private final AbyssEnchantRemoverMenu menu;

        public InputSlot(Container container, int slot, int x, int y, AbyssEnchantRemoverMenu menu) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return menu.canAcceptInput(stack, menu.playerInventory.player);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (menu.blockEntity != null) {
                menu.blockEntity.refreshOutput();
            }
        }
    }

    private static class BookSlot extends Slot {
        private final AbyssEnchantRemoverMenu menu;

        public BookSlot(Container container, int slot, int x, int y, AbyssEnchantRemoverMenu menu) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (menu.blockEntity != null) {
                return menu.blockEntity.canAcceptBook(stack);
            }
            return stack.is(ProjectKTags.Items.BOOKS);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (menu.blockEntity != null) {
                menu.blockEntity.refreshOutput();
            }
        }
    }

    private static class OutputSlot extends Slot {
        private final AbyssEnchantRemoverMenu menu;

        public OutputSlot(Container container, int slot, int x, int y, AbyssEnchantRemoverMenu menu) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if (menu.blockEntity != null) {
                menu.blockEntity.onTakeOutput(player);
            }
            super.onTake(player, stack);
        }
    }

    private static class RemoverContainer extends SimpleContainer {
        private final AbyssEnchantRemoverBlockEntity blockEntity;

        public RemoverContainer(AbyssEnchantRemoverBlockEntity blockEntity) {
            super(CONTAINER_SIZE);
            this.blockEntity = blockEntity;
        }

        @Override
        public ItemStack getItem(int slot) {
            return switch (slot) {
                case SLOT_INPUT -> blockEntity.getInputItem();
                case SLOT_BOOK -> blockEntity.getBookItem();
                case SLOT_OUTPUT -> blockEntity.getOutputItem();
                default -> ItemStack.EMPTY;
            };
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            switch (slot) {
                case SLOT_INPUT -> blockEntity.setInputItem(stack);
                case SLOT_BOOK -> blockEntity.setBookItem(stack);
                case SLOT_OUTPUT -> blockEntity.setOutputItem(stack);
                default -> {
                }
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
}
