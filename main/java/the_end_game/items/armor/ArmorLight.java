package the_end_game.items.armor;

import java.util.*;

import javax.annotation.*;

import net.minecraft.client.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class ArmorLight extends ItemArmor implements IHasModel {
	public ArmorLight(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlot) {
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
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("title.text.not_working"));
	}
	
	/*public static void setBlock(BlockPos pos, World world) {
		if ((world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == BlocksInit.LIGHT_GAS && !world.isRemote)) {
            world.setBlockState(pos, BlocksInit.LIGHT_GAS.getDefaultState());
        }
    }
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
    	EntityPlayer player = (EntityPlayer) entity;
    	if (!world.isDaytime() || world.getLight(new BlockPos(player.posX, player.posY, player.posZ)) <= 6) {
    		if (player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_3 &&
    			player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_3 &&
    			player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_3 &&
    			player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_3) {
    			BlockPos pos = (new BlockPos(entity.posX, entity.posY, entity.posZ)).up();
    			setBlock(pos, world);
    		}
    	}
    }*/
}
