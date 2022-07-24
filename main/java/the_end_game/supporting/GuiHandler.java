package the_end_game.supporting;

import net.minecraft.client.multiplayer.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.*;
import the_end_game.trinket_inventory.container.*;
import the_end_game.trinket_inventory.gui.*;

public class GuiHandler implements IGuiHandler {
	@Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
			case TrinketGui.ID: return new TrinketContainer(player.inventory, !world.isRemote, player);
		}
		return null;
    }
	
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    	if (world instanceof WorldClient) {
			switch (ID) {
				case TrinketGui.ID: return new TrinketGui(player);
			}
		}
		return null;
    }
}
