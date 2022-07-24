package the_end_game.supporting;

import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.registry.*;
import scala.reflect.internal.Trees.This;
import scala.tools.nsc.doc.model.Public;
import the_end_game.Main;
import the_end_game.init.*;
import the_end_game.items.trinkets.CharmHealth;

public class RegCrafts {
	public static void init() {
		mainCraftRegister();
	}
	public static void mainCraftRegister() {
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_1"), null, new ItemStack(ItemsInit.SCYTHE), new Object[] {
				"00*", 
				"2/1", 
				"/12",
						
				'0', Blocks.DIAMOND_BLOCK,
				'*', Items.NETHER_STAR,
				'2', ItemsInit.SOUL_SHARD,//new ItemStack(Items.DYE, 1, 1),
				'/', Items.BLAZE_ROD,
				'1', Items.END_CRYSTAL
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_2"), null, new ItemStack(ItemsInit.HELMET_1), new Object[] {
				"-[-", 
				"202", 
				"-*-",
							
				'-', Items.SHULKER_SHELL,
				'[', new ItemStack(Items.SKULL, 1, 5),
				'2', ItemsInit.SOUL_SHARD,
				'0', Blocks.DIAMOND_BLOCK,
				'*', Items.NETHER_STAR
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_3"), null, new ItemStack(ItemsInit.CHESTPLATE_1), new Object[] {
				"-_-", 
				"202", 
				"-*-",
							
				'-', Items.SHULKER_SHELL,
				'_', Blocks.DRAGON_EGG,
				'2', ItemsInit.SOUL_SHARD,
				'0', Blocks.DIAMOND_BLOCK,
				'*', Items.NETHER_STAR
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_4"), null, new ItemStack(ItemsInit.LEGGINGS_1), new Object[] {
				"131", 
				"202", 
				"-*-",
							
				'1', Blocks.OBSIDIAN,
				'3', Items.END_CRYSTAL,
				'2', ItemsInit.SOUL_SHARD,
				'0', Blocks.DIAMOND_BLOCK,
				'*', Items.NETHER_STAR,
				'-', Items.SHULKER_SHELL
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_5"), null, new ItemStack(ItemsInit.BOOTS_1), new Object[] {
				"-3-", 
				"202", 
				"1*1",
							
				'1', Blocks.OBSIDIAN,
				'3', Items.END_CRYSTAL,
				'2', ItemsInit.SOUL_SHARD,
				'0', Blocks.DIAMOND_BLOCK,
				'*', Items.NETHER_STAR,
				'-', Items.SHULKER_SHELL
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_6"), null, new ItemStack(Blocks.DRAGON_EGG), new Object[] {
				"*0*", 
				"0-0", 
				"_1_",
							
				'0', Blocks.OBSIDIAN,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.SOUL_SHARD,
				'_', ItemsInit.CRYSTAL_DARKNESS,
				'1', Items.TOTEM_OF_UNDYING
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_7"), null, new ItemStack(ItemsInit.AXE), new Object[] {
				"11*", 
				"1/0", 
				"/0 ",
							
				'0', Blocks.OBSIDIAN,
				'1', ItemsInit.CRYSTAL_DARKNESS,
				'/', Items.BLAZE_ROD,
				'*', Items.NETHER_STAR
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_8"), null, new ItemStack(ItemsInit.HELMET_2), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.OBSIDIAN,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_DARKNESS,
				'A', Items.DIAMOND_HELMET
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_9"), null, new ItemStack(ItemsInit.CHESTPLATE_2), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.OBSIDIAN,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_DARKNESS,
				'A', Items.DIAMOND_CHESTPLATE
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_10"), null, new ItemStack(ItemsInit.LEGGINGS_2), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.OBSIDIAN,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_DARKNESS,
				'A', Items.DIAMOND_LEGGINGS
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_11"), null, new ItemStack(ItemsInit.BOOTS_2), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.OBSIDIAN,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_DARKNESS,
				'A', Items.DIAMOND_BOOTS
			}
		);
		
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_12"), null, new ItemStack(ItemsInit.SWORD), new Object[] {
				"121", 
				"0/2", 
				"*01",
							
				'0', Blocks.GOLD_BLOCK,
				'2', Blocks.DIAMOND_BLOCK,
				'1', ItemsInit.CRYSTAL_LIGHT,
				'*', Items.NETHER_STAR,
				'/', Items.BLAZE_ROD
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_13"), null, new ItemStack(ItemsInit.HELMET_3), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.GOLD_BLOCK,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_LIGHT,
				'A', Items.DIAMOND_HELMET
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_14"), null, new ItemStack(ItemsInit.CHESTPLATE_3), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.GOLD_BLOCK,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_LIGHT,
				'A', Items.DIAMOND_CHESTPLATE
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_15"), null, new ItemStack(ItemsInit.LEGGINGS_3), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.GOLD_BLOCK,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_LIGHT,
				'A', Items.DIAMOND_LEGGINGS
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_16"), null, new ItemStack(ItemsInit.BOOTS_3), new Object[] {
				"1+1", 
				"-A-", 
				"0*0",
							
				'0', Blocks.GOLD_BLOCK,
				'1', Blocks.DIAMOND_BLOCK,
				'+', Items.END_CRYSTAL,
				'*', Items.NETHER_STAR,
				'-', ItemsInit.CRYSTAL_LIGHT,
				'A', Items.DIAMOND_BOOTS
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_17"), null, new ItemStack(ItemsInit.SPIDER_BOOTS), new Object[] {
				"/0/", 
				"i-i", 
				"/0/",
							
				'/', ItemsInit.SPIDER_FOOT,
				'0', Items.SLIME_BALL,
				'i', Items.RABBIT_FOOT,
				'-', Items.DIAMOND_BOOTS
			}
		);
		GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_18"), null, new ItemStack(ItemsInit.MROSE_SEED), new Object[] {
				"0*0", 
				"-+-", 
				"|*|",
							
				'0', Items.GLASS_BOTTLE,
				'*', Items.NETHER_STAR,
				'|', Items.DRAGON_BREATH,
				'+', new ItemStack(Blocks.DOUBLE_PLANT, 1, 4),
				'-', ItemsInit.CRYSTAL_DARKNESS
			}
		);
		ItemStack stack_health_1 = new ItemStack(ItemsInit.CHARM_HEALTH);
		ItemNBTHelper.setBoolean(stack_health_1, CharmHealth.LEVEL_1, true);
        GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_19_1"), null, stack_health_1, new Object[] {
                "*0*",
                "0A0",
                "*0*",
                            
                '*', ItemsInit.CRYSTAL_DARKNESS,
                '0', Blocks.OBSIDIAN,
                'A', Items.GOLDEN_APPLE
            }
        );
        ItemStack stack_health_2 = new ItemStack(ItemsInit.CHARM_HEALTH);
        ItemNBTHelper.setBoolean(stack_health_2, CharmHealth.LEVEL_2, true);
        GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_19_2"), null, stack_health_2, new Object[] {
                "0*0",
                "1A1",
                "0*0",
                            
                '0', Blocks.GOLD_BLOCK,
                '1', Blocks.OBSIDIAN,
                'A', Items.GOLDEN_APPLE,
                '*', stack_health_1
            }
        );
        ItemStack stack_health_3 = new ItemStack(ItemsInit.CHARM_HEALTH);
        ItemNBTHelper.setBoolean(stack_health_3, CharmHealth.LEVEL_3, true);
        GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_19_3"), null, stack_health_3, new Object[] {
        		"0*0",
                "*A*",
                "0*0",
                            
                '0', Blocks.EMERALD_BLOCK,
                'A', Items.GOLDEN_APPLE,
                '*', stack_health_2
            }
        );
        GameRegistry.addShapedRecipe(new ResourceLocation(Main.MODID, "recipes/main_cr_teg_20"), null, new ItemStack(ItemsInit.MIRFIRIS_SEED), new Object[] {
        		"*N*",
                "SOS",
                "M#M",
                            
                '*', Items.BLAZE_POWDER,
                'N', Items.NETHER_WART,
                'S', Items.SUGAR,
                'O', new ItemStack(Items.SKULL, 1, 1),
                'M', Items.SPECKLED_MELON,
                '#', Items.WHEAT_SEEDS
            }
        );
	}
}



/*
ItemStack craft_item_1 = new ItemStack(ItemsInit.CHARM_HEALTH, 1, 0);
NBTTagCompound nbt_ci1 = craft_item_1.getTagCompound();
if (nbt_ci1 == null) nbt_ci1 = new NBTTagCompound();
nbt_ci1.setBoolean(CharmHealth.LEVEL_1, true);
craft_item_1.setTagCompound(nbt_ci1);
*/