package gregtech.common.items.armor.gui;

import gregtech.common.items.armor.ArmorData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class ContainerModularArmor extends Container {

	public InventoryArmor mInvArmor;

	public ContainerModularArmor(EntityPlayer player, InventoryArmor aInvArmor) {
		this.mInvArmor = aInvArmor;
		addSlots(player.inventory);
	}
	
	public ArmorData getData(EntityPlayer aPlayer){	
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer aPlayer) {
		return true;
	}

	public abstract void addSlots(InventoryPlayer aInventoryPlayer);

	public abstract int getSlotCount();

	public abstract int getShiftClickSlotCount();

	public void saveInventory(EntityPlayer entityplayer) {
		mInvArmor.onGuiSaved(entityplayer);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (!player.worldObj.isRemote) {
			saveInventory(player);
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		if (player == null) {
			return null;
		}

		ItemStack originalStack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);
		int numSlots = inventorySlots.size();
		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			originalStack = stackInSlot.copy();
			if (slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots)) {
			} else if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
				if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots)) {
					return null;
				}
			} else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
				if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9)) {
					return null;
				}
			} else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots)) {
				return null;
			}
			slot.onSlotChange(stackInSlot, originalStack);
			if (stackInSlot.stackSize <= 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
			if (stackInSlot.stackSize == originalStack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, stackInSlot);
		}
		return originalStack;
	}

	private boolean tryShiftItem(ItemStack stackToShift, int numSlots) {
		for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
			Slot slot = (Slot) inventorySlots.get(machineIndex);
			if (slot.getHasStack()) {
				continue;
			}
			if(slot instanceof SlotLocked){
				continue;
			}
			if(slot instanceof SlotFluid){
				continue;
			}

			if (!slot.isItemValid(stackToShift)) {
				continue;
			}
			if (shiftItemStack(stackToShift, machineIndex, machineIndex + 1)) {
				return true;
			}
		}
		return false;
	}

	protected boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
		boolean changed = false;
		if (stackToShift.isStackable()) {
			for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++) {
				Slot slot = (Slot) inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot != null && isIdenticalItem(stackInSlot, stackToShift)) {
					int resultingStackSize = stackInSlot.stackSize + stackToShift.stackSize;
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					if (resultingStackSize <= max) {
						stackToShift.stackSize = 0;
						stackInSlot.stackSize = resultingStackSize;
						slot.onSlotChanged();
						changed = true;
					} else if (stackInSlot.stackSize < max) {
						stackToShift.stackSize -= max - stackInSlot.stackSize;
						stackInSlot.stackSize = max;
						slot.onSlotChanged();
						changed = true;
					}
				}
			}
		}
		if (stackToShift.stackSize > 0) {
			for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++) {
				Slot slot = (Slot) inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot == null) {
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					stackInSlot = stackToShift.copy();
					stackInSlot.stackSize = Math.min(stackToShift.stackSize, max);
					stackToShift.stackSize -= stackInSlot.stackSize;
					slot.putStack(stackInSlot);
					slot.onSlotChanged();
					changed = true;
				}
			}
		}
		return changed;
	}

	public static boolean isIdenticalItem(ItemStack lhs, ItemStack rhs) {
		if (lhs == null || rhs == null) {
			return false;
		}

		if (lhs.getItem() != rhs.getItem()) {
			return false;
		}

		if (lhs.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
			if (lhs.getItemDamage() != rhs.getItemDamage()) {
				return false;
			}
		}

		return ItemStack.areItemStackTagsEqual(lhs, rhs);
	}

}
