package the_end_game.items.custom;

import java.util.*;

import javax.annotation.*;

import com.google.common.collect.*;

import net.minecraft.client.*;
import net.minecraft.client.util.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;
import the_end_game.entitys.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class MidnightRose extends Item implements IHasModel {
	public static final String NAME = "midnight_rose";
	public static final String TYPE_MOD = "type";
	public static final String BLOOD = "blood";
	public static int MaxBlood = 10000;
	public static int MaxTypeMod = 4;
	
	public static final String DAGGER_MOD = "dagger_mod";
	public static final String RAPIER_MOD = "rapier_mod";
	public static final String SWORD_MOD = "sword_mod";
	public static final String BROADSWORD_MOD = "broadsword_mod";
	
	public MidnightRose() {
		addPropertyOverride(new ResourceLocation(Main.MODID, DAGGER_MOD), (stack, world, entity) -> isDaggerMod(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, RAPIER_MOD), (stack, world, entity) -> isRapierMod(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, SWORD_MOD), (stack, world, entity) -> isSwordMod(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, BROADSWORD_MOD), (stack, world, entity) -> isBroadswordMod(stack) ? 1 : 0);
		
		addPropertyOverride(new ResourceLocation(Main.MODID, "blood_0"), (stack, world, entity) -> isBlood0(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "blood_500"), (stack, world, entity) -> isBlood500(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "blood_1500"), (stack, world, entity) -> isBlood1500(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "blood_3000"), (stack, world, entity) -> isBlood3000(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "blood_5000"), (stack, world, entity) -> isBlood5000(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "blood_7000"), (stack, world, entity) -> isBlood7000(stack) ? 1 : 0);
		
		setUnlocalizedName(NAME);
		setRegistryName(NAME);
		setMaxStackSize(1);
		setCreativeTab(Main.TEG);
		
		ItemsInit.ITEMS.add(this);
	}
	@Override public void registerModels() { Main.proxy.registerItemRenderer(this, 0, "inventory"); }
	
	public boolean hasEffect(ItemStack stack) { return false; }
	
	public static boolean isDaggerMod(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, DAGGER_MOD, false); }
	public static boolean isRapierMod(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, RAPIER_MOD, false); }
	public static boolean isSwordMod(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, SWORD_MOD, false); }
	public static boolean isBroadswordMod(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, BROADSWORD_MOD, false); }
	
	public static boolean isBlood0(ItemStack stack) { if (getBlood(stack) >= 0 && ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) return true; return false; }
	public static boolean isBlood500(ItemStack stack) { if (getBlood(stack) >= 500 && ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) return true; return false; }
	public static boolean isBlood1500(ItemStack stack) { if (getBlood(stack) >= 1500 && ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) return true; return false; }
	public static boolean isBlood3000(ItemStack stack) { if (getBlood(stack) >= 3000 && ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) return true; return false; }
	public static boolean isBlood5000(ItemStack stack) { if (getBlood(stack) >= 5000 && ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) return true; return false; }
	public static boolean isBlood7000(ItemStack stack) { if (getBlood(stack) >= 7000 && ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) return true; return false; }
	
	public static int getBlood(ItemStack stack) { return ItemNBTHelper.getInt(stack, BLOOD, 0); }
	public static void addBlood(EntityPlayer player, ItemStack stack, int count) { ItemNBTHelper.setInt(stack, BLOOD, getBlood(stack) + count); }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("title.text.blood_bar").concat(" " + getBlood(stack)));
		if (isDaggerMod(stack) == true) list.add(I18n.translateToLocalFormatted("title.text.mr_type_mod.1"));
		if (isRapierMod(stack) == true) list.add(I18n.translateToLocalFormatted("title.text.mr_type_mod.2"));
		if (isSwordMod(stack) == true) list.add(I18n.translateToLocalFormatted("title.text.mr_type_mod.3"));
		if (isBroadswordMod(stack) == true) list.add(I18n.translateToLocalFormatted("title.text.mr_type_mod.4"));
	}
	
	@Override
    public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tabs)) {
        	ItemStack stackEmpty = new ItemStack(this);
        	if (this.getBlood(stackEmpty) <= 0) {
        		ItemNBTHelper.setInt(stackEmpty, BLOOD, 0);
        		list.add(stackEmpty);
        	}
        	ItemStack stackFull = new ItemStack(this);
        	ItemNBTHelper.setInt(stackFull, BLOOD, MaxBlood);
        	list.add(stackFull);
        }
	}
	
	@Override public boolean showDurabilityBar(ItemStack stack) { if (getBlood(stack) != 0) return true; return false; }
	@Override public double getDurabilityForDisplay(ItemStack stack) { return 1 - ((double) getBlood(stack) / (double) MaxBlood); }
	@Override public int getRGBDurabilityForDisplay(ItemStack stack) { return MathHelper.rgb(255f, 255f, 0); }
	
	@Override //Анимация дерганья
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}
	
	public Multimap getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
	    Multimap multimap = super.getAttributeModifiers(slot, stack);
	    if (slot == EntityEquipmentSlot.MAINHAND) {
	    	if (isDaggerMod(stack) == true) {
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 20.0f - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 10.0f - 4, 0));
	    	}
	    	if (isRapierMod(stack) == true) {
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 40.0f - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 6.5f - 4, 0));
	    	}
	    	if (isSwordMod(stack) == true) {
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 60.0f - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 1.6f - 4, 0));
	    	}
	    	if (isBroadswordMod(stack) == true) {
	    		Random rand = new Random();
	    		int i = rand.nextInt(100);
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (100.0f + i) - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0.4f - 4, 0));
	    	}
	    }
	    return multimap;
	}
	
	@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		Random rand = new Random();
		if (getBlood(stack) <= 0) {
			ItemNBTHelper.setInt(stack, BLOOD, 0);
			ItemNBTHelper.setInt(stack, TYPE_MOD, 0);
			ItemNBTHelper.setBoolean(stack, MidnightRose.DAGGER_MOD, false);
			ItemNBTHelper.setBoolean(stack, MidnightRose.RAPIER_MOD, false);
			ItemNBTHelper.setBoolean(stack, MidnightRose.SWORD_MOD, false);
			ItemNBTHelper.setBoolean(stack, MidnightRose.BROADSWORD_MOD, false);
		}
		if (getBlood(stack) >= MaxBlood) ItemNBTHelper.setInt(stack, BLOOD, MaxBlood);
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) > MaxTypeMod) ItemNBTHelper.setInt(stack, TYPE_MOD, 0);
		if (rand.nextInt(20*3) == 40) {
			if (isDaggerMod(stack) == true) ItemNBTHelper.setInt(stack, BLOOD, getBlood(stack) - 1);
			if (isRapierMod(stack) == true) ItemNBTHelper.setInt(stack, BLOOD, getBlood(stack) - 10);
			if (isSwordMod(stack) == true) ItemNBTHelper.setInt(stack, BLOOD, getBlood(stack) - 50);
			if (isBroadswordMod(stack) == true) ItemNBTHelper.setInt(stack, BLOOD, getBlood(stack) - 150);	
		}
		//Отмена перекрутки типа предмета
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 1 && isDaggerMod(stack) == false) {ItemNBTHelper.setInt(stack, TYPE_MOD, 0); ItemNBTHelper.setBoolean(stack, DAGGER_MOD, false);}
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 2 && isRapierMod(stack) == false) {ItemNBTHelper.setInt(stack, TYPE_MOD, 0); ItemNBTHelper.setBoolean(stack, RAPIER_MOD, false);}
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 3 && isSwordMod(stack) == false) {ItemNBTHelper.setInt(stack, TYPE_MOD, 0); ItemNBTHelper.setBoolean(stack, SWORD_MOD, false);}
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 4 && isBroadswordMod(stack) == false) {ItemNBTHelper.setInt(stack, TYPE_MOD, 0); ItemNBTHelper.setBoolean(stack, BROADSWORD_MOD, false);}
		//Реген хп
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(player.getHealth() < player.getMaxHealth() && getBlood(stack) >= 500) {
				player.setHealth(player.getHealth() + 1);
				ItemNBTHelper.setInt(stack, BLOOD, getBlood(stack) - 500);
			}
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound nbt = stack.getTagCompound();
		double dx = player.posX;
        double dy = player.posY;
        double dz = player.posZ;
		if(!world.isRemote && player.isSneaking()) {
			if (nbt.hasKey(TYPE_MOD)) nbt.setInteger(TYPE_MOD, nbt.getInteger(TYPE_MOD) + 1); else nbt.setInteger(TYPE_MOD, 0);
			if (nbt.getInteger(TYPE_MOD) == 1 && getBlood(stack) >= 100) {//-------------------------------//
				//for(int i = 1; i < 26; ++i) Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBlood(world, dx + Math.sin((double)i) * 0.5D, dy + 0.2f, dz + Math.cos((double)i) * 0.5D, 0.0D, 0.1188D, 0.0D, 255.0f - i, 0.0f, 0.0f, 2.5f));
				ItemNBTHelper.setBoolean(stack, DAGGER_MOD, true);
				if (!player.isCreative()) player.getCooldownTracker().setCooldown(this, 60*3);
			} else ItemNBTHelper.setBoolean(stack, DAGGER_MOD, false);
			if (nbt.getInteger(TYPE_MOD) == 2 && getBlood(stack) >= 500) {//-------------------------------//
				//for(int i = 1; i < 26; ++i) Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBlood(world, dx + Math.sin((double)i) * 0.5D, dy + 0.2f, dz + Math.cos((double)i) * 0.5D, 0.0D, 0.1188D, 0.0D, 255.0f - i, 0.0f, 0.0f, 2.5f));
				ItemNBTHelper.setBoolean(stack, RAPIER_MOD, true);
				if (!player.isCreative()) player.getCooldownTracker().setCooldown(this, 60*3);
			} else ItemNBTHelper.setBoolean(stack, RAPIER_MOD, false);
			if (nbt.getInteger(TYPE_MOD) == 3 && getBlood(stack) >= 1000) {//-------------------------------//
				//for(int i = 1; i < 26; ++i) Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBlood(world, dx + Math.sin((double)i) * 0.5D, dy + 0.2f, dz + Math.cos((double)i) * 0.5D, 0.0D, 0.1188D, 0.0D, 255.0f - i, 0.0f, 0.0f, 2.5f));
				ItemNBTHelper.setBoolean(stack, SWORD_MOD, true);
				if (!player.isCreative()) player.getCooldownTracker().setCooldown(this, 60*3);
			} else ItemNBTHelper.setBoolean(stack, SWORD_MOD, false);
			if (nbt.getInteger(TYPE_MOD) == 4 && getBlood(stack) >= 2000) {//-------------------------------//
				//for(int i = 1; i < 26; ++i) Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBlood(world, dx + Math.sin((double)i) * 0.5D, dy + 0.2f, dz + Math.cos((double)i) * 0.5D, 0.0D, 0.1188D, 0.0D, 255.0f - i, 0.0f, 0.0f, 2.5f));
				ItemNBTHelper.setBoolean(stack, BROADSWORD_MOD, true);
				if (!player.isCreative()) player.getCooldownTracker().setCooldown(this, 60*3);
			} else ItemNBTHelper.setBoolean(stack, BROADSWORD_MOD, false);
		}
		stack.setTagCompound(nbt);
		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase killedEntity, EntityLivingBase attackingEntity) {
		int i = 3 + killedEntity.world.rand.nextInt(5) + killedEntity.world.rand.nextInt(5);
		int j = EntityBloodBall.getXPSplit(i);
		if (killedEntity instanceof EntityMob || killedEntity instanceof EntityAgeable || killedEntity instanceof EntityAnimal) if (killedEntity != null && killedEntity.isEntityUndead() == false) {
			while (i > 0) { i -= j; killedEntity.world.spawnEntity(new EntityBloodBall(killedEntity.world, killedEntity.posX, killedEntity.posY + 0.5f, killedEntity.posZ, j)); }
			if (attackingEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) attackingEntity;
				if (killedEntity.isEntityAlive() == false) if (player.getHealth() < player.getMaxHealth()) {
					if (isDaggerMod(stack) == true) player.setHealth(player.getHealth() + 2);
					if (isRapierMod(stack) == true) player.setHealth(player.getHealth() + 6);
					if (isSwordMod(stack) == true) player.setHealth(player.getHealth() + 10);
					if (isBroadswordMod(stack) == true) player.setHealth(player.getHealth() + 20);
				}
			}
		}
		if (killedEntity instanceof EntityPlayer) if (killedEntity != null) {
			if (attackingEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) attackingEntity;
				if (player.getHeldItemMainhand().getItem() == ItemsInit.MIDNIGHT_ROSE) ((MidnightRose) stack.getItem()).addBlood(player, stack, 10);
				if (killedEntity.isEntityAlive() == false) if (player.getHealth() < player.getMaxHealth()) {
					if (isDaggerMod(stack) == true) player.setHealth(player.getHealth() + 2);
					if (isRapierMod(stack) == true) player.setHealth(player.getHealth() + 6);
					if (isSwordMod(stack) == true) player.setHealth(player.getHealth() + 10);
					if (isBroadswordMod(stack) == true) player.setHealth(player.getHealth() + 20);
				}
			}
		}
		//drop head
		//Random rand = new Random();rand.nextInt(10) == 0
		if (attackingEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) attackingEntity;
			if (player.getHeldItemMainhand().getItem() == ItemsInit.MIDNIGHT_ROSE && isBroadswordMod(stack) && Math.random() < 0.48f) {
				if (killedEntity instanceof EntitySkeleton) {
					ItemStack head = new ItemStack(Items.SKULL, 1, 0);
					if (!head.isEmpty() && !((EntitySkeleton) killedEntity).world.isRemote) {
						EntityItem entityitem = new EntityItem(((EntitySkeleton) killedEntity).world, (((EntitySkeleton) killedEntity).posX), (((EntitySkeleton) killedEntity).posY + 0.5), (((EntitySkeleton) killedEntity).posZ), head);
				        entityitem.setDefaultPickupDelay();
				        ((EntitySkeleton) killedEntity).world.spawnEntity(entityitem);
					}
				}
				if (killedEntity instanceof EntityWitherSkeleton) {
					ItemStack head = new ItemStack(Items.SKULL, 1, 1);
					if (!head.isEmpty() && !((EntityWitherSkeleton) killedEntity).world.isRemote) {
						EntityItem entityitem = new EntityItem(((EntityWitherSkeleton) killedEntity).world, (((EntityWitherSkeleton) killedEntity).posX), (((EntityWitherSkeleton) killedEntity).posY + 0.5), (((EntityWitherSkeleton) killedEntity).posZ), head);
				        entityitem.setDefaultPickupDelay();
				        ((EntityWitherSkeleton) killedEntity).world.spawnEntity(entityitem);
					}
				}
				if (killedEntity instanceof EntityZombie) {
					ItemStack head = new ItemStack(Items.SKULL, 1, 2);
					if (!head.isEmpty() && !((EntityZombie) killedEntity).world.isRemote) {
						EntityItem entityitem = new EntityItem(((EntityZombie) killedEntity).world, (((EntityZombie) killedEntity).posX), (((EntityZombie) killedEntity).posY + 0.5), (((EntityZombie) killedEntity).posZ), head);
				        entityitem.setDefaultPickupDelay();
				        ((EntityZombie) killedEntity).world.spawnEntity(entityitem);
					}
				}
				if (killedEntity instanceof EntityCreeper) {
					ItemStack head = new ItemStack(Items.SKULL, 1, 4);
					if (!head.isEmpty() && !((EntityCreeper) killedEntity).world.isRemote) {
						EntityItem entityitem = new EntityItem(((EntityCreeper) killedEntity).world, (((EntityCreeper) killedEntity).posX), (((EntityCreeper) killedEntity).posY + 0.5), (((EntityCreeper) killedEntity).posZ), head);
				        entityitem.setDefaultPickupDelay();
				        ((EntityCreeper) killedEntity).world.spawnEntity(entityitem);
					}
				}
				if (killedEntity instanceof EntityPlayer) {
					ItemStack head = new ItemStack(Items.SKULL, 1, 3);
					NBTTagCompound nametag = new NBTTagCompound();
	                nametag.setString("SkullOwner", ((EntityPlayer) killedEntity).getCommandSenderEntity().getName());
	                head.setTagCompound(nametag);
	                if (!head.isEmpty() && !((EntityPlayer) killedEntity).world.isRemote) {
	        			EntityItem entityitem = new EntityItem(((EntityPlayer) killedEntity).world, (((EntityPlayer) killedEntity).posX), (((EntityPlayer) killedEntity).posY + 0.5), (((EntityPlayer) killedEntity).posZ), head);
	        	        entityitem.setDefaultPickupDelay();
	        	        ((EntityPlayer) killedEntity).world.spawnEntity(entityitem);
	        		}
				}
			}
		}
		return true;
	}
}
