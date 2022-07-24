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

public class DepthsRing extends BasicTrinketsItem {
	public DepthsRing(String name, int maxStackSize) { super(name, maxStackSize); }
	
	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.RING; }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("trinket.description.ring_depths"));
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entityBase) {
		if (entityBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityBase;
			boolean swimming = player.isInsideOfMaterial(Material.WATER) || player.isInWater();
			if (swimming && player.experienceLevel >= 1 && !player.isCreative()) {
				float speed = 0.06f * 1.0f;
				if (player.moveForward > 0f) player.moveRelative(0f, 0f, 1f, speed);
				else if (player.moveForward < 0f) player.moveRelative(0f, 0f, 1f, -speed * 0.3f);
				if (player.moveStrafing != 0f) player.moveRelative(1f, 0f, 0f, speed * 0.5f * Math.signum(player.moveStrafing));
				if (player.ticksExisted%180==0) player.experienceLevel -= 1;
			}
			else if (swimming && player.isCreative()) {
				float speed = 0.06f * 1.0f;
				if (player.moveForward > 0f) player.moveRelative(0f, 0f, 1f, speed);
				else if (player.moveForward < 0f) player.moveRelative(0f, 0f, 1f, -speed * 0.3f);
				if (player.moveStrafing != 0f) player.moveRelative(1f, 0f, 0f, speed * 0.5f * Math.signum(player.moveStrafing));
			}
		}
	}
}
