package the_end_game.supporting;

import org.lwjgl.input.*;

import net.minecraft.client.settings.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.client.*;
import net.minecraftforge.fml.client.registry.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.TickEvent.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.supporting.network.*;

public class KeyHandler {
	public static KeyBinding openKey = new KeyBinding("text_button_name", Keyboard.KEY_I, "name_category");

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new KeyHandler());
		ClientRegistry.registerKeyBinding(openKey);
	}
	
	@SubscribeEvent public void playerTick(PlayerTickEvent event) { if (event.side == Side.CLIENT && event.phase == Phase.START ) if (openKey.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) PacketHandler.INSTANCE.sendToServer(new PacketOpenTrinketInventory()); }
}
