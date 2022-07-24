package the_end_game.items.food;

import net.minecraft.item.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.items.*;
import the_end_game.supporting.*;

public class MirfirisFruit extends ItemFood implements IHasModel {
	public MirfirisFruit(String name,int maxStackSize, int amount, float saturation, boolean isWolfFood) {
		super(amount, saturation, isWolfFood);
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(maxStackSize);
		setCreativeTab(Main.TEG);
		
		ItemsInit.ITEMS.add(this);
	}
	@Override public void registerModels() { Main.proxy.registerItemRenderer(this, 0, "inventory"); }
}
