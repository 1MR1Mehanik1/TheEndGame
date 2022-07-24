package the_end_game.potions;

import javax.annotation.*;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import the_end_game.init.*;

public class MirfirisPotion extends BasicPotions {
	public MirfirisPotion(String name, boolean isBadPotion, int colour, int x, int y) {
		super(name, isBadPotion, colour, x, y);
	}
	
	@Override public boolean isInstant() { return true; }
    @Override public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entityBase, int amplifier, double health) {
    	if (this == PotionsInit.MIRFIRIS) {
    		if (source != null) {
    			if (entityBase instanceof EntityPlayer) {
    				EntityPlayer player = (EntityPlayer) entityBase;
    				if (entityBase == player)  {
    					player.getFoodStats().addStats(10, 0F);
    					player.setHealth(player.getHealth() + (player.getMaxHealth() / 2));
    					if (!player.world.isRemote) player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20 * 10, 0, false, true));
    				}
    			}
    			if (entityBase instanceof EntitySkeleton) {
    				EntitySkeleton mob = (EntitySkeleton) entityBase;
    				World world = mob.world;
    				if (world instanceof WorldServer) ((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, mob.posX, mob.posY + 0.1 , mob.posZ, 25, 0.2, 1.4, 0.2, 0.0001, new int[0]);
    				if (entityBase == mob && !world.isRemote && Math.random() < 0.48f) {
    					EntityWitherSkeleton spawnMob = new EntityWitherSkeleton(mob.world);
    					spawnMob.setPositionAndUpdate(mob.posX, mob.posY, mob.posZ);
    					spawnMob.setHeldItem(spawnMob.getActiveHand().MAIN_HAND, new ItemStack(Items.STONE_SWORD));
    					mob.world.spawnEntity(spawnMob);
    					mob.setDead();
    				} else if (entityBase == mob && !world.isRemote) {
    					mob.setDead();
					}
    			}
    			if (entityBase instanceof EntityAnimal) {
    				EntityAnimal mob = (EntityAnimal) entityBase;
    				World world = mob.world;
    				if (world instanceof WorldServer) ((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, mob.posX, mob.posY + 0.1 , mob.posZ, 25, 0.2, 1.4, 0.2, 0.0001, new int[0]);
    				if (entityBase == mob) mob.setGrowingAge(-(8000));
    			}
    			if (entityBase instanceof EntityCreeper) {
    				EntityCreeper mob = (EntityCreeper) entityBase;
    				World world = mob.world;
    				if (world instanceof WorldServer) ((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, mob.posX, mob.posY + 0.1 , mob.posZ, 25, 0.2, 1.4, 0.2, 0.0001, new int[0]);
    				if (entityBase == mob && Math.random() < 0.48f) {
    					mob.onStruckByLightning(null);
    				} else mob.ignite();
    			}
    		}
    	}
    }
}
