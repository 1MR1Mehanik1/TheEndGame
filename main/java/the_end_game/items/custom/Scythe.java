package the_end_game.items.custom;

import java.util.*;

import javax.annotation.*;

import com.google.common.collect.*;

import net.minecraft.client.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.eventhandler.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

@EventBusSubscriber(modid = Main.MODID)
public class Scythe extends ItemSword implements IHasModel {
	public static final String NAME = "reaper_scythe";
	public static final String TYPE_MOD = "type";
	public static int MaxTypeMod = 3;
	
	public static final String FIRE_MOD = "fire_mod";
	public static final String VOID_MOD = "void_mod";
	public static final String SOUL_MOD = "soul_mod";
	
	public Scythe(ToolMaterial material) {
		super(material);
		addPropertyOverride(new ResourceLocation(Main.MODID, "fire_scythe"), (itemStack, world, entity) -> isFire(itemStack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "void_scythe"), (itemStack, world, entity) -> isVoid(itemStack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "soul_scythe"), (itemStack, world, entity) -> isSoul(itemStack) ? 1 : 0);
		
		setUnlocalizedName(NAME);
		setRegistryName(NAME);
		setCreativeTab(Main.TEG);
		
		ItemsInit.ITEMS.add(this);
	}
	@Override public void registerModels() { Main.proxy.registerItemRenderer(this, 0, "inventory"); }
	
	public boolean hasEffect(ItemStack stack) { return false; }
	
	@Override public boolean hasCustomEntity(ItemStack stack) { return true; }
	@Override public Entity createEntity(World world, Entity location, ItemStack itemstack) { location.setEntityInvulnerable(true); return null; }
	public boolean attackEntityFrom(DamageSource source, float amount) { if (!source.damageType.isEmpty()) return false; return false; }
	
	public static boolean isFire(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, FIRE_MOD, false); }
	public static boolean isVoid(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, VOID_MOD, false); }
	public static boolean isSoul(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, SOUL_MOD, false); }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("title.text.scythe_mod"));
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) list.add(I18n.translateToLocalFormatted("title.scythe_mod.0"));
		if (isFire(stack) == true) list.add(I18n.translateToLocalFormatted("title.scythe_mod.1"));
		if (isVoid(stack) == true) list.add(I18n.translateToLocalFormatted("title.scythe_mod.2"));
		if (isSoul(stack) == true) list.add(I18n.translateToLocalFormatted("title.scythe_mod.3"));
	}
	
	public Multimap getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
	    Multimap multimap = super.getAttributeModifiers(slot, stack);
	    if (slot == EntityEquipmentSlot.MAINHAND) {
	    	if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) {
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 350.0f - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0.9f - 4, 0));
	    	}
	    	if (isFire(stack) == true) {
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 150.0f - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0.9f - 4, 0));
	    	}
	    	if (isVoid(stack) == true) {
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 460.0f - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0.9f - 4, 0));
	    	}
	    	if (isSoul(stack) == true) {
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	    		multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
	    		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 60.0f - 1, 0));
	    		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0.9f - 4, 0));
	    	}
	    }
	    return multimap;
	}
	
	@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) <= 0) {
			ItemNBTHelper.setInt(stack, TYPE_MOD, 0);
			ItemNBTHelper.setBoolean(stack, FIRE_MOD, false);
			ItemNBTHelper.setBoolean(stack, VOID_MOD, false);
			ItemNBTHelper.setBoolean(stack, SOUL_MOD, false);
		}
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) > MaxTypeMod) ItemNBTHelper.setInt(stack, TYPE_MOD, 0);
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 1 && isFire(stack) == false) {ItemNBTHelper.setInt(stack, TYPE_MOD, 0); ItemNBTHelper.setBoolean(stack, FIRE_MOD, false);}
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 2 && isVoid(stack) == false) {ItemNBTHelper.setInt(stack, TYPE_MOD, 0); ItemNBTHelper.setBoolean(stack, VOID_MOD, false);}
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 3 && isSoul(stack) == false) {ItemNBTHelper.setInt(stack, TYPE_MOD, 0); ItemNBTHelper.setBoolean(stack, SOUL_MOD, false);}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound nbt = stack.getTagCompound();
		if (!world.isRemote && player.isSneaking()) {
			if (nbt.hasKey(TYPE_MOD)) nbt.setInteger(TYPE_MOD, nbt.getInteger(TYPE_MOD) + 1); else nbt.setInteger(TYPE_MOD, 0);
			if (nbt.getInteger(TYPE_MOD) >= 0) {
				if (!player.isCreative()) player.getCooldownTracker().setCooldown(this, 60*10);
				if (nbt.getInteger(TYPE_MOD) == 1) ItemNBTHelper.setBoolean(stack, FIRE_MOD, true); else ItemNBTHelper.setBoolean(stack, FIRE_MOD, false);
				if (nbt.getInteger(TYPE_MOD) == 2) { ItemNBTHelper.setBoolean(stack, VOID_MOD, true); stack.damageItem(1500, player); } else ItemNBTHelper.setBoolean(stack, VOID_MOD, false);
				if (nbt.getInteger(TYPE_MOD) == 3) ItemNBTHelper.setBoolean(stack, SOUL_MOD, true); else ItemNBTHelper.setBoolean(stack, SOUL_MOD, false);
			}
			//Смена зачарования//--------------------------------------------//
			int phase = ItemNBTHelper.getInt(stack, TYPE_MOD, 0) - 1;
			if (nbt.hasKey("ench")) {
				NBTTagList enchants = stack.getEnchantmentTagList();
				nbt.setTag("enchants" + phase, enchants);
	        } else nbt.removeTag("enchants" + phase);
			
			if (phase++ > MaxTypeMod - 1) phase = 0;
			
			if (nbt.hasKey("enchants" + phase)) {
				NBTTagList enchants_ = (NBTTagList) (nbt.getTag("enchants" + phase));
				nbt.setTag("ench", enchants_);
	        } else nbt.removeTag("ench");
			//--------------------------------------------//
		}
		stack.setTagCompound(nbt);
		return super.onItemRightClick(world, player, hand);
	}
	
	//мы должны поджечь объект как можно раньше, чтобы действительно приготовить еду
	@SubscribeEvent
	public static void setFireBeforeDeath(LivingAttackEvent e) {
		EntityLivingBase killedEntity = (EntityLivingBase) e.getEntity();
		Entity entity = e.getSource().getTrueSource();
		if (entity instanceof EntityPlayer) {
			EntityPlayer attackingEntity = (EntityPlayer) entity;
			if (attackingEntity != killedEntity && attackingEntity.getHeldItemMainhand().getItem() == ItemsInit.SCYTHE && attackingEntity.getHeldItemMainhand().getTagCompound().getInteger(TYPE_MOD) == 1) killedEntity.setFire(1);
		}
	}
	
	@Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase killedEntity, EntityLivingBase attackingEntity) {
		if (ItemNBTHelper.getInt(stack, TYPE_MOD, 0) == 0) if (attackingEntity instanceof EntityPlayer) stack.damageItem(2, attackingEntity);//Повреждение предмета
		if (isFire(stack) == true) {
	        if (!killedEntity.world.isRemote && !killedEntity.isImmuneToFire()) killedEntity.setFire(20 * 15);
			if (attackingEntity instanceof EntityPlayer) stack.damageItem(2, attackingEntity);//Повреждение предмета
		}
		if (isVoid(stack) == true) {
			killedEntity.attackEntityFrom(DamageInit.VOID, 460F);
    		if (attackingEntity instanceof EntityPlayer) stack.damageItem(250, attackingEntity);//Повреждение предмета
		}
		if (isSoul(stack) == true) if (attackingEntity instanceof EntityPlayer) stack.damageItem(2, attackingEntity);//Повреждение предмета
		
		//Система дропа предмета при смерти моба
    	if (killedEntity instanceof EntityVillager || killedEntity instanceof EntityPlayer) if (killedEntity != null && killedEntity.isEntityAlive() == false) {
    		Collection<PotionEffect> effects = ((EntityLivingBase) killedEntity).getActivePotionEffects();
    	    for (PotionEffect effect : effects) if (effect.getPotion() == MobEffects.WEAKNESS) {
    	    	if (!killedEntity.world.isRemote && Math.random() < 0.25f) {
    	    		EntityItem entityToSpawn = new EntityItem(killedEntity.world, (killedEntity.posX), (killedEntity.posY + 0.5), (killedEntity.posZ), new ItemStack(ItemsInit.SOUL_SHARD, 1));
    	    		entityToSpawn.setPickupDelay(10);
    	    		killedEntity.world.spawnEntity(entityToSpawn);
    	        }
    	    }    
    	}
    	//-----------------------------------
		return true;
	}
}
