package the_end_game.trinket_inventory.container;

import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraftforge.items.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

public class TrinketSlot extends SlotItemHandler {
	int trinketSlot;
	EntityPlayer player;

	public TrinketSlot(EntityPlayer player, ITrinketItemHandler itemHandler, int slot, int par4, int par5) {
		super(itemHandler, slot, par4, par5);
		this.trinketSlot = slot;
		this.player = player;
	}
	
	@Override public boolean isItemValid(ItemStack stack) { return ((ITrinketItemHandler) getItemHandler()).isItemValidForSlot(trinketSlot, stack, player); }

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		ItemStack stack = getStack();
		if(stack.isEmpty()) return false;

		ITrinket trinket = stack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
		return trinket.canUnequip(stack, player);
	}

	@Override
	public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
		if (!getHasStack() && !((ITrinketItemHandler) getItemHandler()).isEventBlocked() && stack.hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null)) {
			stack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null).onUnequipped(stack, playerIn);
		}
		super.onTake(playerIn, stack);
		return stack;
	}

	@Override
	public void putStack(ItemStack stack) {
		if (getHasStack() && !ItemStack.areItemStacksEqual(stack,getStack()) && !((ITrinketItemHandler) getItemHandler()).isEventBlocked() && getStack().hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null)) {
			getStack().getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null).onUnequipped(getStack(), player);
		}

		ItemStack oldstack = getStack().copy();
		super.putStack(stack);

		if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack,getStack()) && !((ITrinketItemHandler) getItemHandler()).isEventBlocked() && getStack().hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null)) {
			getStack().getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null).onEquipped(getStack(), player);
		}
	}

	//@Override public int getSlotStackLimit() { return 1; }
}
