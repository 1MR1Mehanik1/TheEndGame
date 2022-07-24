package the_end_game.init;

import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraftforge.fml.common.registry.*;
import the_end_game.potions.*;

public class PotionsInit {
	public static final Potion SHADOW_STEP = new ShadowStep("shadow_step", false, 16777215, 0, 0); //теневой шаг
	public static final PotionType SHADOW_STEP_DEFAULT = new PotionType("shadow_step", new PotionEffect[] {new PotionEffect(SHADOW_STEP, 2400)}).setRegistryName("shadow_step");
	
	public static final Potion MIRFIRIS = new MirfirisPotion("mirfiris_potions", false, 7864449, 0, 0); //настойка мирфирис
	public static final PotionType MIRFIRIS_DEFAULT = new PotionType("mirfiris_potions", new PotionEffect[] {new PotionEffect(MIRFIRIS)}).setRegistryName("mirfiris_potions");
	
	public static void registerPotions() {
		regHelperPotion(SHADOW_STEP, SHADOW_STEP_DEFAULT);
		regHelperPotion(MIRFIRIS, MIRFIRIS_DEFAULT);
		
		regPotionMix();
	}
	
	public static void regHelperPotion(Potion effect, PotionType defaultP) {ForgeRegistries.POTIONS.register(effect); ForgeRegistries.POTION_TYPES.register(defaultP); }
	
	public static void regPotionMix() {
		PotionHelper.addMix(PotionTypes.WEAKNESS, ItemsInit.MIRFIRIS_FRUIT, MIRFIRIS_DEFAULT);
	}
}
