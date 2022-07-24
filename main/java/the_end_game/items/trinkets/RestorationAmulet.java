package the_end_game.items.trinkets;

import java.util.*;

import javax.annotation.*;

import net.minecraft.client.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import the_end_game.items.*;
import the_end_game.trinket_inventory.*;

public class RestorationAmulet extends BasicTrinketsItem {
	public RestorationAmulet(String name, int maxStackSize) { super(name, maxStackSize); }
	
	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.AMULET; }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("trinket.description.amulet_restoration"));
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entityBase) {
		if (entityBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityBase;
			if (player.getHealth() < player.getMaxHealth()) if (player.experienceLevel >= 10 && !player.isCreative()) {
				player.setHealth(player.getHealth() + 1);
				player.experienceLevel -= 10;
			}
		}
	}
}
