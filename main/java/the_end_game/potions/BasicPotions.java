package the_end_game.potions;

import net.minecraft.potion.*;
import net.minecraft.util.*;
import the_end_game.*;

public class BasicPotions extends Potion {
	public BasicPotions (String name, boolean isBadPotion, int colour, int x, int y) {
		super(isBadPotion, colour);
		setPotionName("effect." + name);
		setIconIndex(x, y);
		setRegistryName(new ResourceLocation(Main.MODID + ":" + name));
	}
}
