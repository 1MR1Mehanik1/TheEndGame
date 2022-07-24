package the_end_game.items.trinkets;

import java.util.*;

import javax.annotation.*;

import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import the_end_game.items.*;
import the_end_game.trinket_inventory.*;

public class MirrorVessel extends BasicTrinketsItem {
	public MirrorVessel(String name, int maxStackSize) { 
		super(name, maxStackSize); 
		setMaxDamage(6);
	}
	
	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.AAMULET_CHARM; }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("trinket.description.mirror_vessel"));
	}
}
