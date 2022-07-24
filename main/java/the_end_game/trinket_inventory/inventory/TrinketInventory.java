package the_end_game.trinket_inventory.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.text.*;
import the_end_game.trinket_inventory.cap.*;

public class TrinketInventory implements IInventory {
	public static final String NAME = "trinket_inventory";
	final ITrinketItemHandler handler;
	final EntityPlayer player;

	public TrinketInventory(ITrinketItemHandler handler) { super(); this.handler = handler; this.player = null; }

	public TrinketInventory(ITrinketItemHandler handler, EntityPlayer player) { super(); this.handler = handler; this.player = player; }

	@Override public String getName() { return NAME; }

	@Override public boolean hasCustomName() { return false; }

	@Override public ITextComponent getDisplayName() { return new TextComponentString(this.getName()); }

	@Override public int getSizeInventory() { return handler.getSlots(); }

	@Override public boolean isEmpty() { return false; }

	@Override public ItemStack getStackInSlot(int index) { return handler.getStackInSlot(index); }

	@Override public ItemStack decrStackSize(int index, int count) { return handler.extractItem(index, count, false); }

	@Override public ItemStack removeStackFromSlot(int index) { ItemStack out = this.getStackInSlot(index); handler.setStackInSlot(index, ItemStack.EMPTY); return out; }

	@Override public void setInventorySlotContents(int index, ItemStack stack) { handler.setStackInSlot(index, stack); }

	@Override public int getInventoryStackLimit() { return 64; }

	@Override public void markDirty() {}

	@Override public boolean isUsableByPlayer(EntityPlayer player) { return true; }

	@Override public void openInventory(EntityPlayer player) {}

	@Override public void closeInventory(EntityPlayer player) {}

	@Override public boolean isItemValidForSlot(int index, ItemStack stack) { return handler.isItemValidForSlot(index, stack, player); }

	@Override public int getField(int id) { return 0; }

	@Override public void setField(int id, int value) {}

	@Override public int getFieldCount() { return 0; }

	@Override public void clear() { for (int i = 0; i < this.getSizeInventory(); ++i) this.setInventorySlotContents(i, ItemStack.EMPTY); }
}
