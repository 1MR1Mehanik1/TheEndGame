package the_end_game.supporting.events;

import net.minecraft.client.gui.inventory.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import the_end_game.trinket_inventory.gui.*;

public class GuiEvent {
	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiInventory || event.getGui() instanceof TrinketGui) {
			GuiContainer gui = (GuiContainer) event.getGui();
			event.getButtonList().add(new TrinketGuiButton(40, gui, 76, 16, 20, 20, null));
			
			/*ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
			int x = resolution.getScaledWidth() / 2;
			int y = resolution.getScaledHeight() / 2;
			event.getButtonList().add(new CustomButton(40, gui, x +- 12, y +- 67, 20, 20, null));*/
		}
	}
}
