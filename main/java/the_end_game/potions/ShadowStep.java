package the_end_game.potions;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;
import the_end_game.init.*;

public class ShadowStep extends BasicPotions {
	private final ResourceLocation icon;
	public ShadowStep(String name, boolean isBadPotion, int colour, int x, int y) { 
		super(name, isBadPotion, colour, x, y); 
		icon = new ResourceLocation(Main.MODID, "textures/othes/shadow_step.png"); 
	}
	
	@Override
    public boolean isReady(int duration, int amplifier) { return true; }
	@Override
	public boolean hasStatusIcon() { return false; }
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect potionEffect, Minecraft mc) { if (mc.currentScreen != null) { mc.getTextureManager().bindTexture(icon); Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18); } }
	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect potionEffect, Minecraft mc, float alpha) { mc.getTextureManager().bindTexture(icon); Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18); }
	
	@Override
    public void performEffect(EntityLivingBase target, int amplifier) {
		if (target instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) target;
			World world = player.world;
	        if (player.getActivePotionEffect(PotionsInit.SHADOW_STEP) != null) {
	    		if (world instanceof WorldServer) ((WorldServer) world).spawnParticle(EnumParticleTypes.TOWN_AURA, player.posX, player.posY + 0.1 , player.posZ, 5, 0.1, 0.1, 0.1, 0.0001, new int[0]);
	    		player.setInvisible(true);
	        }
		}
    }
}
