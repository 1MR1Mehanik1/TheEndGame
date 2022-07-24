package the_end_game.trinket_inventory.cap;

import net.minecraft.item.*;
import the_end_game.trinket_inventory.*;

public class TrinketItem implements ITrinket {
	private TrinketType trinketType;

	public TrinketItem(TrinketType type) { trinketType = type; }

	@Override public TrinketType getTrinketType(ItemStack stack) { return trinketType; }
}
