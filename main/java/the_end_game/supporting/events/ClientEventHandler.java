package the_end_game.supporting.events;

import net.minecraft.client.resources.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.client.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.TickEvent.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.supporting.*;
import the_end_game.supporting.network.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

public class ClientEventHandler {
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.CLIENT && event.phase == Phase.START ) if (KeyHandler.openKey.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
			PacketHandler.INSTANCE.sendToServer(new PacketOpenTrinketInventory());
		}
	}

	@SubscribeEvent
	public void tooltipEvent(ItemTooltipEvent event) {
		if (!event.getItemStack().isEmpty() && event.getItemStack().hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null)) {
			ITrinket trinket = event.getItemStack().getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
			TrinketType tt = trinket.getTrinketType(event.getItemStack());
			event.getToolTip().add(I18n.format("trinket.description." + tt));
		}
	}
}
