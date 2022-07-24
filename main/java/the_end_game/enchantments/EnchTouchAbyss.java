package the_end_game.enchantments;

import java.util.*;

import net.minecraft.enchantment.*;
import net.minecraft.enchantment.Enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.item.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import the_end_game.*;
import the_end_game.init.*;

public class EnchTouchAbyss extends Enchantment {
	public EnchTouchAbyss(String name, Rarity rarity, EnumEnchantmentType type, EntityEquipmentSlot... slots) {
		super(rarity, type, slots);
		setEnchantmentName(name);
		
		EnchantmentsInit.ENCHANTMENTS.add(this);
	}
	private void setEnchantmentName(String name) { this.setRegistryName(Main.MODID, name); this.setName(name); }
	
	@Override public int getMaxLevel() { return 3; }
	@Override public int getMinEnchantability(int enchantmentLevel) { return 25; }
    @Override public int getMaxEnchantability(int enchantmentLevel) { return 30; }
    
    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
    	return super.canApplyTogether(enchantment) && enchantment != Enchantments.FIRE_ASPECT; //Несовместимости
    }
    
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
        if (target instanceof EntityWither && target != null && target.isEntityAlive() == false) {
        	Random rand = new Random();
        	if(!target.world.isRemote) {
        		EntityItem drop_1 = new EntityItem(target.world, (target.posX), (target.posY + 0.5), (target.posZ), new ItemStack(ItemsInit.SOUL_SHARD, level));
        		EntityItem drop_2 = new EntityItem(target.world, (target.posX), (target.posY + 0.5), (target.posZ), new ItemStack(ItemsInit.CRYSTAL_DARKNESS, level));
        		EntityItem drop_3 = new EntityItem(target.world, (target.posX), (target.posY + 0.5), (target.posZ), new ItemStack(ItemsInit.CRYSTAL_LIGHT, level));
        		if(Math.random() < 0.4f) {
        			if(rand.nextInt(8) == 0 || rand.nextInt(8) == 1 || rand.nextInt(8) == 2) {
        				drop_1.setPickupDelay(10);
        				target.world.spawnEntity(drop_1);
        			} else if(rand.nextInt(8) == 3 || rand.nextInt(8) == 4 || rand.nextInt(8) == 5) {
        				drop_2.setPickupDelay(8);
        				target.world.spawnEntity(drop_2);
        			} else if(rand.nextInt(8) == 6 || rand.nextInt(8) == 7 || rand.nextInt(8) == 8) {
        				drop_3.setPickupDelay(10);
        				target.world.spawnEntity(drop_3);
        			}
        		}
        	}
        }
    }
}
