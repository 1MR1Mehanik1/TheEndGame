package the_end_game.init;

import java.util.*;

import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.Item.*;
import net.minecraft.item.ItemArmor.*;
import net.minecraftforge.common.util.*;
import the_end_game.*;
import the_end_game.blocks.plants.MirfirisPlant;
import the_end_game.items.*;
import the_end_game.items.armor.*;
import the_end_game.items.custom.*;
import the_end_game.items.food.*;
import the_end_game.items.seeds.*;
import the_end_game.items.trinkets.*;

public class ItemsInit {
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static final Item SOUL_SHARD = new BasicItems("soul_shard", 64);
	public static final Item CRYSTAL_DARKNESS = new BasicItems("crystal_darkness", 64);
	public static final Item CRYSTAL_LIGHT = new BasicItems("crystal_light", 64);
	
	public static final ToolMaterial ToolM_1 = EnumHelper.addToolMaterial("tool_m_1", 50, 50000, 12.5F, 350.0F - 4, 20).setRepairItem(new ItemStack(ItemsInit.SOUL_SHARD));
	public static final ToolMaterial ToolM_2 = EnumHelper.addToolMaterial("tool_m_2", 50, 50000, 12.5F, 150.0F - 4, 20).setRepairItem(new ItemStack(ItemsInit.CRYSTAL_DARKNESS));
	public static final ToolMaterial ToolM_3 = EnumHelper.addToolMaterial("tool_m_3", 50, 50000, 12.5F, 150.0F - 4, 20).setRepairItem(new ItemStack(ItemsInit.CRYSTAL_LIGHT));
	public static final ArmorMaterial ArmorM_1 = EnumHelper.addArmorMaterial("armor_m_1", Main.MODID + ":armor_teg", 1000, new int[] {100, 100, 100, 100}, 20, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 100.0f);
	public static final ArmorMaterial ArmorM_2 = EnumHelper.addArmorMaterial("armor_m_2", Main.MODID + ":armor_dark", 1000, new int[] {15, 20, 20, 15}, 20, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 20.0f);
	public static final ArmorMaterial ArmorM_3 = EnumHelper.addArmorMaterial("armor_m_3", Main.MODID + ":armor_light", 1000, new int[] {15, 20, 20, 15}, 20, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 20.0f);
	
	public static final Item HELMET_1 = new ArmorTEG("helmet_the_end_game", ArmorM_1, 1, EntityEquipmentSlot.HEAD);
	public static final Item CHESTPLATE_1 = new ArmorTEG("chestplate_the_end_game", ArmorM_1, 1, EntityEquipmentSlot.CHEST);
	public static final Item LEGGINGS_1 = new ArmorTEG("leggings_the_end_game", ArmorM_1, 2, EntityEquipmentSlot.LEGS);
	public static final Item BOOTS_1 = new ArmorTEG("boots_the_end_game", ArmorM_1, 1, EntityEquipmentSlot.FEET);
	public static final Item SCYTHE = new Scythe(ToolM_1);
	
	public static final Item HELMET_2 = new ArmorDark("helmet_darkness", ArmorM_2, 1, EntityEquipmentSlot.HEAD);
	public static final Item CHESTPLATE_2 = new ArmorDark("chestplate_darkness", ArmorM_2, 1, EntityEquipmentSlot.CHEST);
	public static final Item LEGGINGS_2 = new ArmorDark("leggings_darkness", ArmorM_2, 2, EntityEquipmentSlot.LEGS);
	public static final Item BOOTS_2 = new ArmorDark("boots_darkness", ArmorM_2, 1, EntityEquipmentSlot.FEET);
	public static final Item AXE = new Axe("axe_darkness", ToolM_2, 150, 4);
	
	public static final Item HELMET_3 = new ArmorLight("helmet_light", ArmorM_3, 1, EntityEquipmentSlot.HEAD);
	public static final Item CHESTPLATE_3 = new ArmorLight("chestplate_light", ArmorM_3, 1, EntityEquipmentSlot.CHEST);
	public static final Item LEGGINGS_3 = new ArmorLight("leggings_light", ArmorM_3, 2, EntityEquipmentSlot.LEGS);
	public static final Item BOOTS_3 = new ArmorLight("boots_light", ArmorM_3, 1, EntityEquipmentSlot.FEET);
	public static final Item SWORD = new Sword("sword_light", ToolM_3, 150, 4);
	
	public static final Item SPIDER_FOOT = new BasicItems("spider_foot", 64);
	public static final Item SPIDER_BOOTS = new SpiderBoots("spider_boots", EnumHelper.addArmorMaterial("s_b", Main.MODID + ":spider", 50, new int[]{6}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 6.0f), 1, EntityEquipmentSlot.FEET);
	
	public static final Item MROSE_SEED = new MRoseSeed("midnight_rose_seed", 16);
	public static final Item MIDNIGHT_ROSE = new MidnightRose();
	
	public static final Item MIRFIRIS_SEED = new MirfirisSeed("mirfiris_seed", 16);
	public static final Item MIRFIRIS_FRUIT = new MirfirisFruit("mirfiris_fruit", 64, 8, 25, false);
	
	//Trinket------------------------------
	public static final Item AMULET_RESTORATION = new RestorationAmulet("amulet_restoration", 1);
	public static final Item RING_FLIGHT = new FlightRing("ring_flight", 1);
	public static final Item RING_WATER_SPEED = new DepthsRing("ring_depths", 1);
	public static final Item RING_LAVA_SPEED = new InfernalBowelsRing("ring_infernal_bowels", 1);
	public static final Item RING_OPPOSITES = new OppositesRing("ring_opposites", 1);
	public static final Item MIRROR_VESSEL = new MirrorVessel("mirror_vessel", 1);
	
	public static final Item CHARM_ARMOR = new CharmArmor("charm_armor", 20);
	public static final Item CHARM_HEALTH = new CharmHealth("charm_health", 20);
	//-------------------------------------
}
