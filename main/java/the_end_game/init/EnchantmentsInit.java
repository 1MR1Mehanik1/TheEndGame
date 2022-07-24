package the_end_game.init;

import java.util.*;

import net.minecraft.enchantment.*;
import net.minecraft.enchantment.Enchantment.*;
import net.minecraft.inventory.*;
import the_end_game.enchantments.*;

public class EnchantmentsInit {
	public static final List<Enchantment> ENCHANTMENTS = new ArrayList<Enchantment>();
	
	public static final Enchantment VOID_DAMAGE = new EnchVoidDamage("void_damage", Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
    public static final Enchantment TOUCH_ABYSS = new EnchTouchAbyss("touch_abyss", Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
}
