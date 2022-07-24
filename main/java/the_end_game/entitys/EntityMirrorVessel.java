package the_end_game.entitys;

import java.util.*;

import javax.annotation.Nullable;

import com.google.common.math.Stats;

import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMirrorVessel extends Entity{
	private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityMirrorVessel.class, DataSerializers.ITEM_STACK);
    private int age;
    private int pickupDelay;
    private int health = 5;
    private String thrower;
    private String owner;
    
    public final float hoverStart = (float)(Math.random() * Math.PI * 2.0D);
    
    public EntityMirrorVessel(World world) {
        super(world);
        this.setSize(0.9F, 0.9F);
    }
    
    public EntityMirrorVessel(World world, double x, double y, double z) {
        super(world);
        this.setPosition(x, y, z);
        this.rotationYaw = this.rand.nextFloat() * 360.0F;
        this.setSize(0.9F, 0.9F);
        
        setNoGravity(true);
    }

    public EntityMirrorVessel(World world, double x, double y, double z, ItemStack stack) {
        this(world, x, y, z);
        this.setSize(0.9F, 0.9F);
        this.setItem(stack);
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    protected void entityInit() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    public void onUpdate() {
    	if (this.getItem().isEmpty()) {
    		this.setDead();
    	} else {
    		super.onUpdate();
    		if (this.pickupDelay > 0 && this.pickupDelay != 32767) --this.pickupDelay;
    		this.prevPosX = this.posX;
    		this.prevPosY = this.posY;
    		this.prevPosZ = this.posZ;
    		double d0 = this.motionX;
            double d1 = this.motionY;
            double d2 = this.motionZ;
           
           
    		if (!this.hasNoGravity()) {
    			this.motionY -= 0.04d;
    		}
    		if (this.world.isRemote) {
    			this.noClip = false;
    			world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY+(this.getEyeHeight()/2), this.posZ, ((Math.random()-0.5)*2.0), ((Math.random()-0.5)*2.0), ((Math.random()-0.5)*2.0));
    		}
    		++this.age;
    		
    		if (!this.world.isRemote) {
                double d3 = this.motionX - d0;
                double d4 = this.motionY - d1;
                double d5 = this.motionZ - d2;
                double d6 = d3 * d3 + d4 * d4 + d5 * d5;

                if (d6 > 0.01D) {
                    this.isAirBorne = true;
                }
            }
    		
    		ItemStack item = this.getItem();
           
    		if (item.isEmpty()) {
    			this.setDead();
    		}
    	}
    }

    protected void dealFireDamage(int amount) {
        this.attackEntityFrom(DamageSource.IN_FIRE, (float)amount);
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
    	if (this.world.isRemote || !this.isEntityAlive()) return false;
    	if (source.isDamageAbsolute()) {
    		this.setDead();
    		return false;
    	} else return false;
    }
    
    public static void registerFixesItem(DataFixer fixer) {
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityItem.class, new String[] {"Item"}));
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (short)this.health);
        compound.setShort("Age", (short)this.age);
        compound.setShort("PickupDelay", (short)this.pickupDelay);

        if (this.getThrower() != null) {
            compound.setString("Thrower", this.thrower);
        }
        if (this.getOwner() != null) {
            compound.setString("Owner", this.owner);
        }
        if (!this.getItem().isEmpty()) {
            compound.setTag("Item", this.getItem().writeToNBT(new NBTTagCompound()));
        }
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");

        if (compound.hasKey("PickupDelay")) {
            this.pickupDelay = compound.getShort("PickupDelay");
        }
        if (compound.hasKey("Owner")) {
            this.owner = compound.getString("Owner");
        }
        if (compound.hasKey("Thrower")) {
            this.thrower = compound.getString("Thrower");
        }
        
        NBTTagCompound nbttagcompound = compound.getCompoundTag("Item");
        this.setItem(new ItemStack(nbttagcompound));

        if (this.getItem().isEmpty()) {
            this.setDead();
        }
    }
    
	public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.world.isRemote) {
           if (this.pickupDelay > 0) return;
           ItemStack itemstack = this.getItem();
           Item item = itemstack.getItem();
           int i = itemstack.getCount();
           
           if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUniqueID()))) {
        	   if (itemstack.isEmpty()) {}
        	   this.setDead();
           }
           
           /*ItemStack copy = itemstack.copy();
           if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUniqueID())) && (i <= 0 || player.inventory.addItemStackToInventory(itemstack))) {
        	   copy.setCount(copy.getCount() - getItem().getCount());
        	   if (itemstack.isEmpty()) {
        		   player.onItemPickup(this, i);
                  EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(this.posX, this.posY, this.posZ, 64, this.dimension)), new PacketHandleItemPickup(player.getEntityId(), this.getEntityId()));
        		   this.setDead();
        		   itemstack.setCount(i);
        	   }
               player.addStat(Stats.ITEM_PICKED_UP.get(item), i);
           }*/
        }
    }

    public String getName() {
        return this.hasCustomName() ? this.getCustomNameTag() : I18n.translateToLocal("item." + this.getItem().getUnlocalizedName());
    }
    
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Nullable
    public Entity changeDimension(int dimensionIn, net.minecraftforge.common.util.ITeleporter teleporter) {
        Entity entity = super.changeDimension(dimensionIn, teleporter);
        return entity;
    }
    
    public ItemStack getItem() {
        return (ItemStack)this.getDataManager().get(ITEM);
    }
    
    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
    }

    public String getOwner() {
        return this.owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getThrower() {
        return this.thrower;
    }
    public void setThrower(String thrower) {
        this.thrower = thrower;
    }

    @SideOnly(Side.CLIENT)
    public int getAge() {
        return this.age;
    }

    public void setDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void setNoPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setInfinitePickupDelay() {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int ticks) {
        this.pickupDelay = ticks;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void makeFakeItem() {
        this.setInfinitePickupDelay();
    }
}
