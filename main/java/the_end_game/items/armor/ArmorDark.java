package the_end_game.items.armor;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.item.ItemArmor.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class ArmorDark extends ItemArmor implements IHasModel {
	public ArmorDark(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlot) {
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
	
	@SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot armourSlot, ModelBiped original) {
		if ((armourSlot == EntityEquipmentSlot.HEAD || armourSlot == EntityEquipmentSlot.CHEST || armourSlot == EntityEquipmentSlot.LEGS || armourSlot == EntityEquipmentSlot.FEET) && !entityLiving.isInvisible()) return null; else return Main.proxy.getArmourModel(getArmorMaterial());
    }
	
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_2 && 
				player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_2 && 
				player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_2 && 
				player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_2) {
			player.fallDistance = 0.0F;
			
			if (player.getActivePotionEffect(MobEffects.BLINDNESS) != null) player.removePotionEffect(MobEffects.BLINDNESS);
			if (player.isSneaking() && world.getLight(new BlockPos(player.posX, player.posY, player.posZ)) <= 6) if (!player.world.isRemote && !player.isInvisible()) player.addPotionEffect(new PotionEffect(PotionsInit.SHADOW_STEP, 20 * 60, 0, false, false));
			if (!world.isDaytime() && !player.isInvisible() && player.isSneaking()) player.addPotionEffect(new PotionEffect(PotionsInit.SHADOW_STEP, 20 * 30, 0, false, false));
			if (player.getPosition().getY() < -30) this.teleportWallSafe(player, world, new BlockPos(player.getPosition().getX(), 255, player.getPosition().getZ()));
		}
	}
	
	public static void teleportWallSafe(EntityLivingBase player, World world, double x, double y, double z) {
        BlockPos coords = new BlockPos(x, y, z);

        world.markBlockRangeForRenderUpdate(coords, coords);
        player.setPositionAndUpdate(x, y, z);
        moveEntityWallSafe(player, world);
    }
	public static void teleportWallSafe(EntityLivingBase player, World world, BlockPos coords) {
        teleportWallSafe(player, world, (double) coords.getX(), (double) coords.getY(), (double) coords.getZ());
    }
	public static void moveEntityWallSafe(EntityLivingBase entity, World world) {
        while (world.collidesWithAnyBlock(entity.getEntityBoundingBox())) entity.setPositionAndUpdate(entity.posX, entity.posY + 1.0D, entity.posZ);
    }
}
