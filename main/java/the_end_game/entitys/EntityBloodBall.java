package the_end_game.entitys;

import net.minecraft.block.material.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.init.ItemsInit;
import the_end_game.items.custom.*;

public class EntityBloodBall extends Entity {
	public int xpColor;
    public int xpOrbAge;
    public int delayBeforeCanPickup;
    private int xpOrbHealth = 5;
    public int xpValue;
    private EntityPlayer closestPlayer;
    private EntityPlayerSP playerSP;
    private int xpTargetColor;

    public EntityBloodBall(World world, double x, double y, double z, int expValue) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.setPosition(x, y, z);
        this.rotationYaw = (float) (Math.random() * 360.0D);
        this.motionX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.motionY = (double) ((float) (Math.random() * 0.2D) * 2.0F);
        this.motionZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.xpValue = expValue;
    }

    protected boolean canTriggerWalking() { return false; }

    public EntityBloodBall(World world) { super(world); this.setSize(0.25F, 0.25F); }

    protected void entityInit() {}

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
        float f = 0.5F;

        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightnessForRender();
        int j = i & 255;
        int k = i >> 16 & 255;

        j += (int) (f * 15.0F * 16.0F);
        if (j > 240) j = 240;

        return j | k << 16;
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.delayBeforeCanPickup > 0) --this.delayBeforeCanPickup;

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (!this.hasNoGravity()) this.motionY -= 0.029999999329447746D;

        if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
            this.motionY = 0.20000000298023224D;
            this.motionX = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.motionZ = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
        double d0 = 8.0D;

        if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
            if (this.closestPlayer == null || this.closestPlayer.getDistanceSq(this) > 64.0D) this.closestPlayer = this.world.getClosestPlayerToEntity(this, 8.0D);
            this.xpTargetColor = this.xpColor;
        }

        if (this.closestPlayer != null && this.closestPlayer.isSpectator()) this.closestPlayer = null;
        if (this.closestPlayer != null && this.closestPlayer.getHeldItemMainhand().getItem() != ItemsInit.MIDNIGHT_ROSE) this.closestPlayer = null;

        if (this.closestPlayer != null) {
            double f = (this.closestPlayer.posX - this.posX) / 8.0D;
            double d2 = (this.closestPlayer.posY + (double) this.closestPlayer.getEyeHeight() / 2.0D - this.posY) / 8.0D;
            double d3 = (this.closestPlayer.posZ - this.posZ) / 8.0D;
            double d4 = Math.sqrt(f * f + d2 * d2 + d3 * d3);
            double d5 = 1.0D - d4;

            if (d5 > 0.0D) {
                d5 *= d5;
                this.motionX += f / d4 * d5 * 0.1D;
                this.motionY += d2 / d4 * d5 * 0.1D;
                this.motionZ += d3 / d4 * d5 * 0.1D;
            }
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        float f = 0.98F;

        if (this.onGround) f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.98F;
        
        this.motionX *= (double) f;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double) f;
        if (this.onGround) this.motionY *= -0.8999999761581421D;
        
        ++this.xpColor;
        ++this.xpOrbAge;
        if (this.xpOrbAge >= 6000) this.setDead();
    }

    public boolean handleWaterMovement() { return this.world.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this); }

    protected void dealFireDamage(int amount) { this.attackEntityFrom(DamageSource.IN_FIRE, (float) amount); }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.world.isRemote && !this.isDead) {
            if (this.isEntityInvulnerable(source)) return false;
            else {
                this.markVelocityChanged();
                this.xpOrbHealth = (int) ((float) this.xpOrbHealth - amount);
                if (this.xpOrbHealth <= 0) this.setDead();
                return false;
            }
        } else return false;
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (short) this.xpOrbHealth);
        compound.setShort("Age", (short) this.xpOrbAge);
        compound.setShort("Value", (short) this.xpValue);
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        this.xpOrbHealth = compound.getShort("Health");
        this.xpOrbAge = compound.getShort("Age");
        this.xpValue = compound.getShort("Value");
    }
    
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.world.isRemote && this.delayBeforeCanPickup == 0 && player.xpCooldown == 0) {
            player.xpCooldown = 2;
            player.onItemPickup(this, 1);
            ItemStack stack_r = player.getHeldItem(EnumHand.MAIN_HAND);
            ItemStack stack_l = player.getHeldItem(EnumHand.OFF_HAND);
            
            if (stack_r.getItem() instanceof MidnightRose) { ((MidnightRose) stack_r.getItem()).addBlood(player, stack_r, this.xpValue); this.setDead(); }
            if (stack_l.getItem() instanceof MidnightRose) { ((MidnightRose) stack_l.getItem()).addBlood(player, stack_l, this.xpValue); this.setDead(); } //System.out.println(MidnightRose.getBlood(stack_r));
        }
    }
    
    private int durabilityToXp(int durability) { return durability / 2; }

    private int xpToDurability(int xp) { return xp * 2; }

    public int getXpValue() { return this.xpValue; }

    @SideOnly(Side.CLIENT)
    public int getTextureByXP() { return this.xpValue >= 2477 ? 10 : (this.xpValue >= 1237 ? 9 : (this.xpValue >= 617 ? 8 : (this.xpValue >= 307 ? 7 : (this.xpValue >= 149 ? 6 : (this.xpValue >= 73 ? 5 : (this.xpValue >= 37 ? 4 : (this.xpValue >= 17 ? 3 : (this.xpValue >= 7 ? 2 : (this.xpValue >= 3 ? 1 : 0))))))))); }

    public static int getXPSplit(int expValue) { return expValue >= 2477 ? 2477 : (expValue >= 1237 ? 1237 : (expValue >= 617 ? 617 : (expValue >= 307 ? 307 : (expValue >= 149 ? 149 : (expValue >= 73 ? 73 : (expValue >= 37 ? 37 : (expValue >= 17 ? 17 : (expValue >= 7 ? 7 : (expValue >= 3 ? 3 : 1))))))))); }

    public boolean canBeAttackedWithItem() { return false; }
}
