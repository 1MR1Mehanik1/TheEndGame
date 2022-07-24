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

public class FlightRing extends BasicTrinketsItem {
	public FlightRing(String name, int maxStackSize) { super(name, maxStackSize); }
	
	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.RING; }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("trinket.description.ring_flight"));
	}
	
	@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (!player.isCreative()) {
				player.capabilities.allowFlying = false; 
				player.capabilities.isFlying = false;
			}
		}
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entityBase) {
		if (entityBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityBase;
			if (!player.isCreative()) {
				if (player.experienceLevel >= 6) player.capabilities.allowFlying = true; else { player.capabilities.allowFlying = false; player.capabilities.isFlying = false; }
				if (player.capabilities.isFlying && player.ticksExisted%80==0) {
					player.experienceLevel -= 1;
				}
			} 
		}
	}
}
