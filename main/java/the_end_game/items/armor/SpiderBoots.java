package the_end_game.items.armor;

import java.util.*;

import javax.annotation.*;

import net.minecraft.client.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class SpiderBoots extends ItemArmor implements IHasModel {
	public SpiderBoots(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlot) {
		super(material, renderIndex, equipmentSlot);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.TEG);
		
		ItemsInit.ITEMS.add(this);
	}
	@Override public void registerModels() { Main.proxy.registerItemRenderer(this, 0, "inventory"); }
	
	public boolean hasEffect(ItemStack stack) { return false; }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("title.text.spider_boots"));
	}
	
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (player.inventory.armorInventory.get(0).getItem() == ItemsInit.SPIDER_BOOTS) {
			player.fallDistance = 0.0F;
			if (player.collidedHorizontally) {
				this.tryMakeEntityClimb(world, player, 0.288D);
				if (!player.world.isRemote) stack.damageItem(1, player);
			}
		}
	}
	public static void tryMakeEntityClimb(World worldIn, EntityLivingBase entity, double climbSpeed) {
        if (entity.isSneaking()) entity.motionY = 0.0D; else if (entity.moveForward > 0.0F && entity.motionY < climbSpeed) entity.motionY = climbSpeed;
    }
}
