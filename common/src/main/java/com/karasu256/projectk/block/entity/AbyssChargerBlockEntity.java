package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssCharger;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.IEnergyItemInput;
import com.karasu256.projectk.energy.IEnergyItemOutput;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssChargerMenu;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AbyssChargerBlockEntity extends AbstractAbyssMachineBlockEntity implements MenuProvider, ICableInputable, IEnergyItemInput, IEnergyItemOutput {
    private long transferRate;

    public AbyssChargerBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_CHARGER.get(), pos, state, ProjectKMachineCapacities.ABYSS_CHARGER);
        this.transferRate = resolveTransferRate(state);
        addItemSlot(Id.id("input"));
        addItemSlot(Id.id("output"));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssChargerBlockEntity be) {
        if (level.isClientSide) {
            return;
        }
        be.serverTick();
    }


    private static long resolveTransferRate(BlockState state) {
        if (state.getBlock() instanceof AbyssCharger charger) {
            return charger.getTransferRate();
        }
        return 0L;
    }

    @Override
    public int getMaxTier() {
        return 3;
    }

    public long getTieredTransferRate() {
        return transferRate * getTier();
    }

    private void serverTick() {
        setInputItem(chargeFromItem(getInputItem()));
        setOutputItem(chargeItem(getOutputItem()));
    }

    @Override
    public ItemStack chargeFromItem(ItemStack input) {
        if (input.isEmpty()) {
            return input;
        }
        List<AbyssEnergyData> energies = AbyssEnergyData.readEnergyList(input);
        if (energies.isEmpty()) {
            return input;
        }
        int selectedIndex = findPriorityIndex(energies);
        if (selectedIndex < 0) {
            return input;
        }
        AbyssEnergyData data = energies.get(selectedIndex);
        if (!canAcceptEnergy(data.energyId())) {
            return input;
        }
        long room = getEnergyCapacity() - getEnergyAmount();
        if (room <= 0) {
            return input;
        }
        long maxMove = getTieredTransferRate() > 0 ? Math.min(getTieredTransferRate(), room) : room;
        long toMove = Math.min(maxMove, data.amountOrZero());
        if (toMove <= 0) {
            return input;
        }
        long inserted = insert(data.energyId(), toMove, false);
        if (inserted <= 0) {
            return input;
        }
        long remaining = data.amountOrZero() - inserted;
        if (remaining <= 0) {
            energies.remove(selectedIndex);
        } else {
            energies.set(selectedIndex, new AbyssEnergyData(data.energyId(), remaining));
        }
        AbyssEnergyData.writeEnergyList(input, energies);
        return input;
    }

    @Override
    public ItemStack chargeItem(ItemStack output) {
        if (output.isEmpty()) {
            return output;
        }
        EnergyCapacityData capacityData = output.get(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get());
        if (capacityData == null || capacityData.isInfinite() || (capacityData.get() != null && capacityData.get() <= 0)) {
            if (getEnergyCapacity() <= 0) {
                return output;
            }
            capacityData = EnergyCapacityData.of(getEnergyCapacity());
            output.set(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get(), capacityData);
        }
        ResourceLocation energyId = getAbyssEnergyId();
        if (energyId == null || getEnergyAmount() <= 0) {
            return output;
        }
        List<AbyssEnergyData> energies = AbyssEnergyData.readEnergyList(output);
        int existingIndex = findEnergyIndex(energies, energyId);
        long current = existingIndex >= 0 ? energies.get(existingIndex).amountOrZero() : 0L;
        long cap = capacityData.get() != null ? capacityData.get() : Long.MAX_VALUE;
        if (current >= cap) {
            return output;
        }
        long maxMove = getTieredTransferRate() > 0 ? Math.min(getTieredTransferRate(), cap - current) : (cap - current);
        long toMove = Math.min(maxMove, getEnergyAmount());
        if (toMove <= 0) {
            return output;
        }
        long extracted = extract(energyId, toMove, false);
        if (extracted <= 0) {
            return output;
        }
        long nextAmount = current + extracted;
        if (existingIndex >= 0) {
            energies.set(existingIndex, new AbyssEnergyData(energyId, nextAmount));
        } else {
            energies.add(new AbyssEnergyData(energyId, nextAmount));
        }
        AbyssEnergyData.writeEnergyList(output, energies);
        return output;
    }

    public ItemStack getInputItem() {
        return heldItems.getFirst().getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        heldItems.getFirst().setHeldItem(stack);
        markDirtyAndSync();
    }

    public ItemStack getOutputItem() {
        return heldItems.get(1).getHeldItem();
    }

    public void setOutputItem(ItemStack stack) {
        heldItems.get(1).setHeldItem(stack);
        markDirtyAndSync();
    }

    @Override
    public boolean canAcceptEnergyItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        List<AbyssEnergyData> energies = AbyssEnergyData.readEnergyList(stack);
        if (energies.isEmpty()) {
            return false;
        }
        for (AbyssEnergyData data : energies) {
            if (data == null || data.energyId() == null || !data.hasPositiveAmount()) {
                continue;
            }
            if (canAcceptEnergy(data.energyId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canChargeItem(ItemStack stack) {
        return !stack.isEmpty();
    }

    private int findPriorityIndex(List<AbyssEnergyData> list) {
        long maxAmount = -1L;
        int bestIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data == null || data.energyId() == null || !data.hasPositiveAmount()) {
                continue;
            }
            long amount = data.amountOrZero();
            if (amount > maxAmount) {
                maxAmount = amount;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private int findEnergyIndex(List<AbyssEnergyData> list, ResourceLocation id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data != null && id.equals(data.energyId())) {
                return i;
            }
        }
        return -1;
    }

    public int getDataValue(int index) {
        return switch (index) {
            case 0 -> (int) getEnergyAmount();
            case 1 -> (int) (getEnergyAmount() >>> 32);
            case 2 -> (int) (long) getEnergyCapacity();
            case 3 -> (int) (long) (getEnergyCapacity() >>> 32);
            case 4 -> getAbyssEnergyId() == null ? 0 : ProjectKEnergies.getModelIndex(getAbyssEnergyId());
            default -> 0;
        };
    }

    public void setDataValue(int index, int value) {
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_charger");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssChargerMenu(syncId, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putLong("transfer_rate", transferRate);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("transfer_rate")) {
            transferRate = nbt.getLong("transfer_rate");
        }
    }
}