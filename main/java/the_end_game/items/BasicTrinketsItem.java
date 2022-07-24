package the_end_game.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;
import the_end_game.trinket_inventory.container.TrinketSlot;

public class BasicTrinketsItem extends BasicItems implements ITrinket {
	public BasicTrinketsItem(String name, int maxStackSize) { super(name, maxStackSize); }

	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.NORMAL; }
	
	@Override public void onEquipped(ItemStack itemstack, EntityLivingBase player) { player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 1.9f); }

	@Override public void onUnequipped(ItemStack itemstack, EntityLivingBase player) { player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 2f); }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			ITrinketItemHandler trinkets = TrinketAPI.getTrinketHandler(player);
			for (int i = 0; i < trinkets.getSlots(); i++) if ((trinkets.getStackInSlot(i) == null || trinkets.getStackInSlot(i).isEmpty()) && trinkets.isItemValidForSlot(i, player.getHeldItem(hand), player)) {
				trinkets.setStackInSlot(i, player.getHeldItem(hand).copy());
				if (!player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
				}
				onEquipped(player.getHeldItem(hand), player);
				break;
			}
		}
		world.playSound(player, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundCategory.MASTER, .75F, 1.9f);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
