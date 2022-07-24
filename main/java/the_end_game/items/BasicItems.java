package the_end_game.items;

import net.minecraft.item.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class BasicItems extends Item implements IHasModel {
	public BasicItems(String name,int maxStackSize) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(maxStackSize);
		setCreativeTab(Main.TEG);
		
		ItemsInit.ITEMS.add(this);
	}
	@Override public void registerModels() { Main.proxy.registerItemRenderer(this, 0, "inventory"); }
}
