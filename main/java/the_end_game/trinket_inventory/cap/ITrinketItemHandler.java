package the_end_game.trinket_inventory.cap;

import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraftforge.items.*;

public interface ITrinketItemHandler extends IItemHandlerModifiable {
	public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player);

	public boolean isEventBlocked();
	public void setEventBlock(boolean blockEvents);

	boolean isChanged(int slot);
	void setChanged(int slot, boolean changed);

	public void setPlayer(EntityLivingBase player);
}
