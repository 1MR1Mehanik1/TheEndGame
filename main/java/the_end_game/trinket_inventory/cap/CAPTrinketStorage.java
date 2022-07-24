package the_end_game.trinket_inventory.cap;

import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraftforge.items.*;
import the_end_game.trinket_inventory.*;

public class CAPTrinketStorage extends ItemStackHandler implements ITrinketItemHandler {
	private final static int TRINKET_SLOTS = 10;
	private boolean[] changed = new boolean[TRINKET_SLOTS];
	private boolean blockEvents = false;	
	private EntityLivingBase player;

	public CAPTrinketStorage() { super(TRINKET_SLOTS); }

	@Override
	public void setSize(int size) {
		if (size<TRINKET_SLOTS) size = TRINKET_SLOTS;
		super.setSize(size);
		boolean[] old = changed;
		changed = new boolean[size];
		for(int i = 0; i < old.length && i < changed.length; i++) changed[i] = old[i];
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player) {
		if (stack == null || stack.isEmpty() || !stack.hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null)) return false;
		ITrinket trinket = stack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
		return trinket.canEquip(stack, player) && trinket.getTrinketType(stack).hasSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (stack == null || stack.isEmpty() || this.isItemValidForSlot(slot, stack, player)) super.setStackInSlot(slot, stack);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!this.isItemValidForSlot(slot, stack, player)) return stack;
		return super.insertItem(slot, stack, simulate);
	}

	@Override public boolean isEventBlocked() { return blockEvents; }

	@Override public void setEventBlock(boolean blockEvents) { this.blockEvents = blockEvents; }

	@Override protected void onContentsChanged(int slot) { setChanged(slot,true); }

	@Override public boolean isChanged(int slot) { if (changed == null) changed = new boolean[this.getSlots()]; return changed[slot]; }

	@Override public void setChanged(int slot, boolean change) { if (changed==null) changed = new boolean[this.getSlots()]; this.changed[slot] = change; }

	@Override public void setPlayer(EntityLivingBase player) { this.player=player; }
}
