package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.menu.AbyssEnchanterMenu;
import com.karasu256.projectk.utils.Id;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;

public class AbyssEnchanterScreen extends AbstractContainerScreen<AbyssEnchanterMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/enchanting_table.png");
    private static final int OPTION_X = 60;
    private static final int OPTION_Y = 14;
    private static final int OPTION_WIDTH = 108;
    private static final int OPTION_HEIGHT = 19;
    private static final int OPTION_NAME_WIDTH = OPTION_WIDTH - 12;

    private final Component[] cachedNames = new Component[3];
    private ItemStack cachedInput = ItemStack.EMPTY;
    private final int[] cachedLevels = new int[]{0, 0, 0};
    private final int[] cachedCosts = new int[]{0, 0, 0};

    public AbyssEnchanterScreen(AbyssEnchanterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderOptions(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
        renderEnergyTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        EnergyBarRenderer.renderFluidBar(graphics, menu.getEnergyId(), menu.getEnergy(), menu.getEnergyCapacity(), leftPos + 10, topPos + 20, 8, 50);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < 3; i++) {
                if (isHoveringOption(i, (int) mouseX, (int) mouseY) && canEnchantOption(i)) {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderOptions(GuiGraphics graphics, int mouseX, int mouseY) {
        refreshOptionNames();
        for (int i = 0; i < 3; i++) {
            int level = menu.getTierLevel(i);
            int cost = menu.getTierCost(i);
            if (level <= 0 || cost <= 0) {
                continue;
            }
            int x = leftPos + OPTION_X;
            int y = topPos + OPTION_Y + OPTION_HEIGHT * i;
            int color;
            if (!canEnchantOption(i)) {
                color = 0xFF707070;
            } else if (isHoveringOption(i, mouseX, mouseY)) {
                color = 0xFFFFFFA0;
            } else {
                color = 0xFFB0B0B0;
            }
            Component name = cachedNames[i];
            if (name == null) {
                name = Component.empty();
            }
            graphics.drawString(font, name, x + 10, y + 6, color, false);
        }
    }

    private void refreshOptionNames() {
        ItemStack input = menu.getSlot(0).getItem();
        boolean inputChanged = !ItemStack.isSameItemSameComponents(input, cachedInput);
        boolean optionsChanged = false;
        for (int i = 0; i < 3; i++) {
            int level = menu.getTierLevel(i);
            int cost = menu.getTierCost(i);
            if (level != cachedLevels[i] || cost != cachedCosts[i]) {
                optionsChanged = true;
                cachedLevels[i] = level;
                cachedCosts[i] = cost;
            }
        }
        if (!inputChanged && !optionsChanged) {
            return;
        }
        cachedInput = input.copy();
        for (int i = 0; i < 3; i++) {
            if (menu.getTierLevel(i) > 0 && menu.getTierCost(i) > 0) {
                cachedNames[i] = Component.literal(EnchantmentNames.getInstance().getRandomName(font, OPTION_NAME_WIDTH).getString());
            } else {
                cachedNames[i] = Component.empty();
            }
        }
    }

    private boolean canEnchantOption(int index) {
        int level = menu.getTierLevel(index);
        int cost = menu.getTierCost(index);
        if (level <= 0 || cost <= 0) {
            return false;
        }
        if (menu.getEnergy() < cost) {
            return false;
        }
        if (menu.getSlot(1).hasItem()) {
            return false;
        }
        var input = menu.getSlot(0).getItem();
        if (input.isEmpty() || !menu.getSlot(0).mayPlace(input)) {
            return false;
        }
        ItemEnchantments enchants = input.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments stored = input.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        return enchants.isEmpty() && stored.isEmpty();
    }

    private boolean isHoveringOption(int index, int mouseX, int mouseY) {
        int x = OPTION_X;
        int y = OPTION_Y + OPTION_HEIGHT * index;
        return isHovering(x, y, OPTION_WIDTH, OPTION_HEIGHT, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        for (int i = 0; i < 3; i++) {
            if (isHoveringOption(i, mouseX, mouseY)) {
                Component tooltip = buildOptionTooltip(i);
                if (tooltip != null) {
                    graphics.renderTooltip(font, tooltip, mouseX, mouseY);
                }
                return;
            }
        }
    }

    private Component buildOptionTooltip(int index) {
        int enchantId = menu.getOptionEnchantmentId(index);
        int level = menu.getOptionEnchantmentLevel(index);
        if (enchantId < 0 || level <= 0) {
            return null;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return null;
        }
        var registry = client.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        java.util.Optional<Holder.Reference<Enchantment>> holder = registry.getHolder(enchantId);
        if (holder.isEmpty()) {
            return null;
        }
        return Enchantment.getFullname(holder.get(), level);
    }

    private void renderEnergyTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isHovering(10, 20, 8, 50, mouseX, mouseY)) {
            var tooltip = EnergyBarRenderer.toolTip(menu.getEnergyId(), menu.getEnergy(), menu.getEnergyCapacity());
            if (!tooltip.isEmpty()) {
                graphics.renderTooltip(font, tooltip, mouseX, mouseY);
            }
        }
    }
}
