package the_end_game.trinket_inventory;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import the_end_game.trinket_inventory.cap.*;
import the_end_game.trinket_inventory.inventory.*;

public class TrinketAPI {
	public static ITrinketItemHandler getTrinketHandler(EntityPlayer player) {
		ITrinketItemHandler handler = player.getCapability(CAPTrinket.INV_CAP_TRINKET, null);
		handler.setPlayer(player);
		return handler;
	}
	@Deprecated
	public static IInventory getTrinkets(EntityPlayer player) {
		ITrinketItemHandler handler = player.getCapability(CAPTrinket.INV_CAP_TRINKET, null);
		handler.setPlayer(player);
		return new TrinketInventory(handler, player);
	}
	public static int isTrinketEquipped(EntityPlayer player, Item trinket) {
		ITrinketItemHandler handler = getTrinketHandler(player);
		for (int e = 0; e < handler.getSlots(); e++) if (!handler.getStackInSlot(e).isEmpty() && handler.getStackInSlot(e).getItem() == trinket) return e;
		return -1;
	}
}
