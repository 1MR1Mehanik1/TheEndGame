package the_end_game.items.trinkets;

import java.util.*;

import javax.annotation.*;

import com.google.common.collect.*;

import net.minecraft.client.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import the_end_game.items.*;
import the_end_game.supporting.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;
import the_end_game.trinket_inventory.container.*;

public class CharmArmor extends BasicTrinketsItem {
	public CharmArmor(String name, int maxStackSize) { super(name, maxStackSize); }
	
	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.CHARM_ARMOR; }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("trinket.description.charm_armor"));
	}
}
