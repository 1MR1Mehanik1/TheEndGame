package the_end_game.trinket_inventory;

import net.minecraft.entity.*;
import net.minecraft.item.*;

public interface ITrinket {
	public TrinketType getTrinketType(ItemStack itemstack);

	public default void onWornTick(ItemStack itemstack, EntityLivingBase player) {}
	
	public default void onEquipped(ItemStack itemstack, EntityLivingBase player) {}
	
	public default void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}
	
	public default boolean canEquip(ItemStack itemstack, EntityLivingBase player) { return true; }
	
	public default boolean canUnequip(ItemStack itemstack, EntityLivingBase player) { return true; }
	
	public default boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) { return false; }
}
