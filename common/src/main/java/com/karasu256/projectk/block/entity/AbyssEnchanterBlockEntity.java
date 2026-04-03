package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssEnchanter;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.data.AbyssEnchanterTier;
import com.karasu256.projectk.data.AbyssEnchanterTierManager;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.enchant.ProjectKEnchantments;
import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssEnchanterMenu;
import com.karasu256.projectk.registry.ProjectKTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbyssEnchanterBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> implements MenuProvider {
    private static final int CRAFT_TIME = 100;
    private int progress = 0;
    private ItemStack outputItem = ItemStack.EMPTY;

    public AbyssEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ENCHANTER.get(), pos, state, resolveCapacity(state));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssEnchanterBlockEntity be) {
        if (level.isClientSide) return;
        be.serverTick();
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssEnchanter enchanter) {
            return enchanter.getCapacity();
        }
        return 0L;
    }

    @Override
    protected AbyssEnergy createEnergy() {
        return new AbyssEnergy(0L);
    }

    private void serverTick() {
        ItemStack input = getInputItem();
        if (input.isEmpty() || !isValidInput(input)) {
            resetProgress();
            return;
        }

        AbyssEnchanterTier tier = selectTier();
        if (tier == null) {
            resetProgress();
            return;
        }

        if (!canAcceptOutput()) {
            resetProgress();
            return;
        }

        int previous = progress;
        progress++;
        if (progress != previous) {
            setChanged();
            sync();
        }

        if (progress >= CRAFT_TIME) {
            enchant(input, tier);
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
        setChanged();
        sync();
    }

    private AbyssEnchanterTier selectTier() {
        List<AbyssEnchanterTier> tiers = AbyssEnchanterTierManager.getTiers();
        if (tiers.isEmpty()) {
            return null;
        }
        return AbyssEnchanterTierManager.getBestTier(getAmount());
    }

    private boolean canAcceptOutput() {
        return outputItem.isEmpty();
    }

    private void enchant(ItemStack input, AbyssEnchanterTier tier) {
        ResourceLocation energyId = getAbyssEnergyId();
        if (energyId == null) {
            return;
        }
        long extracted = extract(energyId, tier.cost(), false);
        if (extracted < tier.cost()) {
            return;
        }

        ItemStack result = buildResult(input, tier);
        if (result.isEmpty()) {
            return;
        }

        if (input.getCount() == 1) {
            setInputItem(ItemStack.EMPTY);
        } else {
            ItemStack next = input.copy();
            next.shrink(1);
            setInputItem(next);
        }

        outputItem = result;
        setChanged();
        sync();
    }

    private ItemStack buildResult(ItemStack input, AbyssEnchanterTier tier) {
        ItemStack result;
        if (input.is(ProjectKTags.Items.BOOKS) || input.is(Items.BOOK)) {
            result = new ItemStack(Items.ENCHANTED_BOOK);
        } else {
            result = input.copy();
            result.setCount(1);
        }

        var enchantmentRegistry = level == null ? null : level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (enchantmentRegistry == null) {
            return ItemStack.EMPTY;
        }
        var enchantment = enchantmentRegistry.getOrThrow(ProjectKEnchantments.ABYSS_BOOSTER_KEY);
        boolean isBook = result.is(Items.ENCHANTED_BOOK);
        var component = isBook ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
        ItemEnchantments base = result.getOrDefault(component, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(base);
        mutable.set(enchantment, tier.level());
        result.set(component, mutable.toImmutable());
        AbyssEnergyData.applyToStack(result, getAbyssEnergyId(), tier.cost() / 2);
        return result;
    }

    public ItemStack getInputItem() {
        return getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        setHeldItem(stack);
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public void setOutputItem(ItemStack stack) {
        outputItem = stack;
        setChanged();
        sync();
    }

    public int getDataValue(int index) {
        return switch (index) {
            case 0 -> progress;
            case 1 -> CRAFT_TIME;
            case 2 -> (int) getAmount();
            case 3 -> (int) (getAmount() >>> 32);
            case 4 -> (int) getCapacity();
            case 5 -> (int) (getCapacity() >>> 32);
            case 6 -> getAbyssEnergyId() == null ? 0 : ProjectKEnergies.getModelIndex(getAbyssEnergyId());
            default -> 0;
        };
    }

    public void setDataValue(int index, int value) {
        if (index == 0) {
            progress = value;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_enchanter");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssEnchanterMenu(syncId, inventory, this);
    }

    private boolean isValidInput(ItemStack stack) {
        if (stack.is(ProjectKTags.Items.BOOKS) || stack.is(Items.BOOK)) {
            return true;
        }
        return stack.is(net.minecraft.tags.ItemTags.AXES)
                || stack.is(net.minecraft.tags.ItemTags.HOES)
                || stack.is(net.minecraft.tags.ItemTags.PICKAXES)
                || stack.is(net.minecraft.tags.ItemTags.SHOVELS)
                || stack.is(net.minecraft.tags.ItemTags.SWORDS)
                || stack.is(net.minecraft.tags.ItemTags.TRIMMABLE_ARMOR)
                || stack.is(Items.MACE)
                || stack.is(Items.TRIDENT);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt(EnergyKeys.MAGIC_TABLE_PROGRESS.toString(), progress);
        if (!outputItem.isEmpty()) {
            nbt.put(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString(), outputItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        progress = nbt.getInt(EnergyKeys.MAGIC_TABLE_PROGRESS.toString());
        if (nbt.contains(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString())) {
            outputItem = ItemStack.parse(registries, nbt.getCompound(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString())).orElse(ItemStack.EMPTY);
        } else {
            outputItem = ItemStack.EMPTY;
        }
    }
}
