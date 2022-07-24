package the_end_game.items.trinkets;

import java.util.*;

import javax.annotation.*;

import net.minecraft.block.material.*;
import net.minecraft.client.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import the_end_game.items.*;
import the_end_game.trinket_inventory.*;

public class OppositesRing extends BasicTrinketsItem {
	public OppositesRing(String name, int maxStackSize) { super(name, maxStackSize); }
	
	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.RING; }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("trinket.description.ring_opposites"));
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entityBase) {
		if (entityBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityBase;
			boolean swimming_w = player.isInsideOfMaterial(Material.WATER) || player.isInWater();
			boolean swimming_l = player.isInsideOfMaterial(Material.LAVA) || player.isInLava();
			if ((swimming_w || swimming_l) && player.experienceLevel >= 2 && !player.isCreative()) {
				float speed = 0.06f * (swimming_l ? 1.2f : 1.0f);
				if (player.moveForward > 0f) player.moveRelative(0f, 0f, 1f, speed);
				else if (player.moveForward < 0f) player.moveRelative(0f, 0f, 1f, -speed * 0.3f);
				if (player.moveStrafing != 0f) player.moveRelative(1f, 0f, 0f, speed * 0.5f * Math.signum(player.moveStrafing));
				if (player.ticksExisted%120==0) player.experienceLevel -= 2;
			}
			else if ((swimming_w || swimming_l) && player.isCreative()) {
				float speed = 0.06f * (swimming_l ? 1.2f : 1.0f);
				if (player.moveForward > 0f) player.moveRelative(0f, 0f, 1f, speed);
				else if (player.moveForward < 0f) player.moveRelative(0f, 0f, 1f, -speed * 0.3f);
				if (player.moveStrafing != 0f) player.moveRelative(1f, 0f, 0f, speed * 0.5f * Math.signum(player.moveStrafing));
			}
		}
	}
}
