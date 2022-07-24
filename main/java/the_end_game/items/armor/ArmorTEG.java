package the_end_game.items.armor;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.block.material.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class ArmorTEG extends ItemArmor implements IHasModel {
	public ArmorTEG(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlot) {
		super(material, renderIndex, equipmentSlot);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.TEG);
		
		ItemsInit.ITEMS.add(this);
	}
	@Override public void registerModels() { Main.proxy.registerItemRenderer(this, 0, "inventory"); }
	
	public boolean hasEffect(ItemStack stack) { return false; }
	
	@Override public boolean hasCustomEntity(ItemStack stack) { return true; }
	@Override public Entity createEntity(World world, Entity location, ItemStack itemstack) { location.setEntityInvulnerable(true); return null; }
	public boolean attackEntityFrom(DamageSource source, float amount) { if (!source.damageType.isEmpty()) return false; return false; }
	
	public Multimap getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap multimap = super.getAttributeModifiers(slot, stack);
		UUID uuid = new UUID(slot.toString().hashCode(), 0);
		if (slot == armorType) multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(uuid, "Terrasteel modifier " + slot, (double) damageReduceAmount / 20, 0));
		return multimap;
	}
	
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if(player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_1 && 
				player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_1 && 
				player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_1 && 
				player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_1) {
			player.fallDistance = 0.0F;
			player.extinguish();
			if (player.world.isRemote) player.stepHeight = player.isSneaking() ? 0.5F : 1F;
			boolean flying = player.capabilities.isFlying;
			boolean swimming = player.isInsideOfMaterial(Material.WATER) || player.isInWater();
			if (player.onGround || flying || swimming) {
				float speed = 0.06f * (flying ? 1.05f : 1.0f);
				if (player.moveForward > 0f) player.moveRelative(0f, 0f, 1f, speed);
				else if (player.moveForward < 0f) player.moveRelative(0f, 0f, 1f, -speed * 0.3f);
				if (player.moveStrafing != 0f) player.moveRelative(1f, 0f, 0f, speed * 0.5f * Math.signum(player.moveStrafing));
			}
			player.setAir(300);
			player.capabilities.allowFlying = true;
		} else {
			player.stepHeight = 0.5F;
			player.capabilities.allowFlying = false;
			player.capabilities.isFlying = false;
		}
		if(!player.isCreative()) if(player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_1 || 
				player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_1 || 
				player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_1 || 
				player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_1) {
			if(world.getBlockState(new BlockPos(player.posX, player.posY - 1, player.posZ)).getBlock() == Blocks.BEDROCK.getDefaultState().getBlock() || player.getPosition().getY() <= -5) {
				if(Math.random() < 0.5) player.attackEntityFrom(DamageInit.VOID, 2.0f);
			}
		}
	}
}
