package the_end_game.enchantments;

import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import the_end_game.*;
import the_end_game.init.*;

public class EnchVoidDamage extends Enchantment {
	public EnchVoidDamage(String name, Rarity rarity, EnumEnchantmentType type, EntityEquipmentSlot... slots) {
		super(rarity, type, slots);
		setEnchantmentName(name);
		
		EnchantmentsInit.ENCHANTMENTS.add(this);
	}
	private void setEnchantmentName(String name) { this.setRegistryName(Main.MODID, name); this.setName(name); }
	
	@Override public int getMaxLevel() { return 1; }
	@Override public int getMinEnchantability(int enchantmentLevel) { return 20; }
    @Override public int getMaxEnchantability(int enchantmentLevel) { return 30; }
    
    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
    	return super.canApplyTogether(enchantment) && enchantment != Enchantments.FIRE_ASPECT && enchantment != Enchantments.LOOTING; //Несовместимости
    }
    
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
        if (target instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) target;
            entitylivingbase.attackEntityFrom(DamageInit.VOID, 30.0f);
            ItemStack stack = user.getHeldItemMainhand();
            if (user instanceof EntityPlayer) stack.damageItem(100, user);
        }
    }
}
