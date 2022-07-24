package the_end_game.supporting.events;

import java.util.*;

import net.minecraft.advancements.*;
import net.minecraft.client.resources.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.item.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.TickEvent.*;
import the_end_game.entitys.*;
import the_end_game.init.*;
import the_end_game.items.custom.*;
import the_end_game.items.trinkets.*;
import the_end_game.supporting.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

public class Event {
	@SubscribeEvent
	public void livingHurt(LivingHurtEvent e) {
		DamageSource source = e.getSource();
		Entity entity = e.getEntity();
		Entity entityAttacker = e.getSource().getTrueSource();
		Entity killedEntity = e.getEntity();
		
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			EnumHand hand = player.getActiveHand();
			ItemStack stack = player.getHeldItem(hand);
			
			if (player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_1 && 
					player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_1 && 
					player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_1 && 
					player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_1) {
				e.setCanceled(true);
				player.setHealth(player.getMaxHealth());
			}
			if (player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_1 || 
					player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_1 || 
					player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_1 || 
					player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_1) if (source == DamageInit.VOID) for (int i = 0; i < 4; i++) player.inventory.armorInventory.get(i).damageItem(1000, player);
			if (player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_2 && 
					player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_2 && 
					player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_2 && 
					player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_2) if (source == DamageSource.CACTUS || source == DamageSource.MAGIC || source == DamageSource.DRAGON_BREATH || source == DamageSource.WITHER) e.setCanceled(true);
			
			if (player.getHeldItem(hand).getItem() == ItemsInit.SWORD && player.isHandActive() && stack.getItemUseAction() == EnumAction.BLOCK) if (entity instanceof EntityLivingBase) if ((entityAttacker instanceof EntityLivingBase) && ((EntityLivingBase) entityAttacker).getHeldItem(((EntityLivingBase) entityAttacker).getActiveHand()).getItem() == ItemsInit.AXE) if (!entity.world.isRemote) {
				((EntityLivingBase) entityAttacker).addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20 * 30, 0, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20 * 30, 0, false, false));
							
				((EntityLivingBase) entityAttacker).knockBack(entityAttacker, 2.5f, entityAttacker.motionX, entityAttacker.motionZ);
							
				((WorldServer) entity.world).spawnParticle(EnumParticleTypes.CRIT, player.posX, player.posY + 1 , player.posZ, 30, 0.7, 0.7, 0.7, 0.0001, new int[0]);
				player.getCooldownTracker().setCooldown(player.getActiveItemStack().getItem(), 60 * 2);
			}
			if (player.getHeldItem(hand).getItem() == ItemsInit.AXE && player.isHandActive() && stack.getItemUseAction() == EnumAction.BLOCK) if (entity instanceof EntityLivingBase) if ((entityAttacker instanceof EntityLivingBase) && ((EntityLivingBase) entityAttacker).getHeldItem(((EntityLivingBase) entityAttacker).getActiveHand()).getItem() == ItemsInit.SWORD) if (!entity.world.isRemote) {
				((EntityLivingBase) entityAttacker).addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20 * 30, 0, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20 * 30, 0, false, false));
							
				((EntityLivingBase) entityAttacker).knockBack(entityAttacker, 2.5f, entityAttacker.motionX, entityAttacker.motionZ);
							
				((WorldServer) entity.world).spawnParticle(EnumParticleTypes.CRIT, player.posX, player.posY + 1 , player.posZ, 30, 0.7, 0.7, 0.7, 0.0001, new int[0]);
				player.getCooldownTracker().setCooldown(player.getActiveItemStack().getItem(), 60 * 2);	
			}
		}
		
		if ((entityAttacker instanceof EntityLivingBase) && ((EntityLivingBase) entityAttacker).getHeldItem(((EntityLivingBase) entityAttacker).getActiveHand()).getItem() == Items.SHEARS) if (killedEntity instanceof EntitySpider) if (!killedEntity.world.isRemote) {
			((EntitySpider) killedEntity).attackEntityFrom(DamageSource.CRAMMING, 10);
			EntityItem entityToSpawn = new EntityItem(killedEntity.world, (killedEntity.posX), (killedEntity.posY + 0.5), (killedEntity.posZ), new ItemStack(ItemsInit.SPIDER_FOOT, 1));
			entityToSpawn.setPickupDelay(10);
			if (Math.random() < 0.3f) killedEntity.world.spawnEntity(entityToSpawn);
			//Random rand = new Random(); //for (int i = 0; i < rand.nextInt(6); i++) { количество выподаемых предметов//}
		}
	}
	
	public UUID ARMOR_UUID = UUID.fromString("3548342c-1bcb-4d61-aae5-3cf4330f02a9");
	public UUID HEALTH_UUID = UUID.fromString("3548342c-1bcb-4d61-aae5-3cf4330f02a3");
	
	public void setArmor(EntityPlayer player, Double value) {
        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.ARMOR);
        AttributeModifier modifier = attribute.getModifier(ARMOR_UUID);
        if (modifier != null) attribute.removeModifier(modifier);
        if (value != 0) attribute.applyModifier(new AttributeModifier(ARMOR_UUID, "armor", value, 0).setSaved(true));
    }
	public void setHealth(EntityPlayer player, Double value) {
        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        AttributeModifier modifier = attribute.getModifier(HEALTH_UUID);
        if (modifier != null) attribute.removeModifier(modifier);
        if (value != 0) attribute.applyModifier(new AttributeModifier(HEALTH_UUID, "health", value, 0).setSaved(true));
    }
	
	@SubscribeEvent
	public void livingUpdate(LivingUpdateEvent e) {
		Entity entity = e.getEntity();
		World world = entity.world;
		int range = 10;
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_2 && 
				player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_2 && 
				player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_2 && 
				player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_2) if (player.isSneaking() && world.getLight(new BlockPos(player.posX, player.posY, player.posZ)) <= 6) { 
					//список ентити в радиусе игрока
					AxisAlignedBB axisalignedbb = (new AxisAlignedBB(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range));
					List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(entity, axisalignedbb); 
					for (Entity target : list) if (!list.isEmpty()) {//--- 
						if (target instanceof EntityPlayer || target instanceof EntityMob || target instanceof EntityAnimal) ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20 * 60, 0, true, true));
						if (target instanceof EntityPlayer) if (((EntityPlayer) target).inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_2 && 
								((EntityPlayer) target).inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_2 &&
								((EntityPlayer) target).inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_2 &&
								((EntityPlayer) target).inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_2) ((EntityPlayer) target).addPotionEffect(new PotionEffect(PotionsInit.SHADOW_STEP, 20 * 60, 0, false, false));
					}
			}
			ITrinketItemHandler trinkets_hand = TrinketAPI.getTrinketHandler(player);
			for (int i = 0; i < trinkets_hand.getSlots(); i++) {
				ItemStack stack = trinkets_hand.getStackInSlot(i);
				ITrinket trinket = stack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
				//CharmArmor----------
				if (trinket == ItemsInit.CHARM_ARMOR && stack != null) {
					for (int s = 0; s < stack.getMaxStackSize() + 1; s++) if (stack.getCount() == s) setArmor(player, (double) (s * 4));
				} else if (trinkets_hand.getStackInSlot(9).getItem() != ItemsInit.CHARM_ARMOR) {
					IAttributeInstance attribute_arm = player.getEntityAttribute(SharedMonsterAttributes.ARMOR);
					AttributeModifier modifier_arm = attribute_arm.getModifier(ARMOR_UUID);
					if (attribute_arm.getModifier(ARMOR_UUID) != null) attribute_arm.removeModifier(modifier_arm);
				}
				//CharmHealth---------
				if (trinket == ItemsInit.CHARM_HEALTH && stack != null) {
					for (int s = 0; s < stack.getMaxStackSize() + 1; s++) if (stack.getCount() == s) {
						if (CharmHealth.isLevel_1(stack)) setHealth(player, (double) (s * 2));
						if (CharmHealth.isLevel_2(stack)) setHealth(player, (double) (s * 4));
						if (CharmHealth.isLevel_3(stack)) setHealth(player, (double) (s * 6));
					}
				} else if (trinkets_hand.getStackInSlot(8).getItem() != ItemsInit.CHARM_HEALTH) {
					IAttributeInstance attribute_h = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
					AttributeModifier modifier_h = attribute_h.getModifier(HEALTH_UUID);
					
					float diff = player.getMaxHealth() - player.getHealth();
					float amount = MathHelper.clamp(player.getMaxHealth() - diff, 0.0f, player.getMaxHealth());
					
					if (attribute_h.getModifier(HEALTH_UUID) != null && player != null) {
						attribute_h.removeModifier(modifier_h);
						player.setHealth(amount);
					}
				}
				
			}
		}
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP) entity;
			AdvancementProgress ap_3 = playerMP.getAdvancements().getProgress(playerMP.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("the_end_game:advance_3")));
			AdvancementProgress ap_4 = playerMP.getAdvancements().getProgress(playerMP.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("the_end_game:advance_4")));
			AdvancementProgress ap_5 = playerMP.getAdvancements().getProgress(playerMP.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("the_end_game:advance_5")));
			AdvancementProgress ap_6 = playerMP.getAdvancements().getProgress(playerMP.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("the_end_game:advance_6")));
			AdvancementProgress ap_7 = playerMP.getAdvancements().getProgress(playerMP.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("the_end_game:advance_7")));
			AdvancementProgress ap_8 = playerMP.getAdvancements().getProgress(playerMP.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("the_end_game:advance_8")));
			if (ap_3.isDone() && ap_4.isDone() && ap_5.isDone() && ap_6.isDone() && ap_7.isDone() && ap_8.isDone()) {
				Advancement ad_9 = playerMP.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("the_end_game:advance_9"));
				AdvancementProgress ap_9 = playerMP.getAdvancements().getProgress(ad_9);
				if (!ap_9.isDone()) {
					Iterator iterator = ap_9.getRemaningCriteria().iterator();
					while (iterator.hasNext()) {
						String criterion = (String) iterator.next();
						playerMP.getAdvancements().grantCriterion(ad_9, criterion);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerDropItem(ItemTossEvent e) {
		EntityItem eItem = e.getEntityItem();
		if (eItem != null) {
			ItemStack stack = eItem.getItem();
			Item item = stack.getItem();
			if (item instanceof MidnightRose && stack.getTagCompound().hasKey(MidnightRose.TYPE_MOD) && stack.getTagCompound().hasKey(MidnightRose.BLOOD)) {
				ItemNBTHelper.setInt(stack, MidnightRose.TYPE_MOD, 0);
				ItemNBTHelper.setBoolean(stack, MidnightRose.DAGGER_MOD, false);
				ItemNBTHelper.setBoolean(stack, MidnightRose.RAPIER_MOD, false);
				ItemNBTHelper.setBoolean(stack, MidnightRose.SWORD_MOD, false);
				ItemNBTHelper.setBoolean(stack, MidnightRose.BROADSWORD_MOD, false);
			}
		}
	}
	
	@SubscribeEvent
    public void livingTarget(LivingSetAttackTargetEvent e) {
		if (e.getEntityLiving().isEntityUndead() && e.getTarget() != null && e.getTarget().isPotionActive(PotionsInit.SHADOW_STEP)) {
	        EntityLiving entity = (EntityLiving) e.getEntityLiving();
	        entity.setAttackTarget(null);
        }
	}
	
	@SubscribeEvent
	public void onDeath(LivingDeathEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			ITrinketItemHandler trinkets_hand = TrinketAPI.getTrinketHandler(player);
			for (int i = 0; i < trinkets_hand.getSlots(); i++) {
				ItemStack stack = trinkets_hand.getStackInSlot(i);
				ITrinket trinket = stack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
				if (trinket == ItemsInit.MIRROR_VESSEL && !player.world.getGameRules().getBoolean("keepInventory")) {
					stack.damageItem(1, player);
					if (!player.world.isRemote) {
						EntityMirrorVessel spawnEntity = new EntityMirrorVessel(player.world, player.posX, player.posY + 1.2f, player.posZ,  new ItemStack(ItemsInit.MIRROR_VESSEL, 1));
						spawnEntity.setPickupDelay(10);
						player.world.spawnEntity(spawnEntity);
					}
				}
			}
		}
	}
}
