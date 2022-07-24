package the_end_game.items.custom;

import java.util.*;
import java.util.Map.*;

import javax.annotation.*;

import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class Axe extends ItemSword implements IHasModel {
public static final String INVIS_MOD = "invis_mod";
	
	public Axe(String name, ToolMaterial material, float damage, float speed) {
		super(material);
		addPropertyOverride(new ResourceLocation(Main.MODID, "invis_axe"), (stack, world, entity) -> isInvisAxe(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "blocking"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) { return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1.0F : 0.0F; }
        });
		
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
	
	@Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.BLOCK; }
	@Override public int getMaxItemUseDuration(ItemStack stack) { return 72000; }
	
	private boolean isInvisAxe(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, INVIS_MOD, false); }
	
	@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.isInvisible() && 
					player.inventory.armorInventory.get(3).getItem() == ItemsInit.HELMET_2 && 
					player.inventory.armorInventory.get(2).getItem() == ItemsInit.CHESTPLATE_2 && 
					player.inventory.armorInventory.get(1).getItem() == ItemsInit.LEGGINGS_2 && 
					player.inventory.armorInventory.get(0).getItem() == ItemsInit.BOOTS_2) ItemNBTHelper.setBoolean(stack, INVIS_MOD, true); else ItemNBTHelper.setBoolean(stack, INVIS_MOD, false);
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		player.setActiveHand(hand);
        return super.onItemRightClick(world, player, hand);
    }
	
	@Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase killedEntity, EntityLivingBase attackingEntity) {
		stack.damageItem(2, attackingEntity);//Повреждение предмета
		if (killedEntity instanceof EntityEnderman || killedEntity instanceof EntityEndermite || killedEntity instanceof EntityShulker) if (killedEntity != null && killedEntity.isEntityAlive() == false && !killedEntity.world.isDaytime() || killedEntity.world.getBiome(new BlockPos(killedEntity.posX, killedEntity.posY, killedEntity.posZ)) == Biomes.SKY) 
			if ((killedEntity instanceof EntityEnderman && Math.random() < 0.1) || (killedEntity instanceof EntityEndermite && Math.random() < 0.3) || (killedEntity instanceof EntityShulker && Math.random() < 0.4)) if (!killedEntity.world.isRemote) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
				for (Entry<Enchantment, Integer> entry : map.entrySet()) {
					Enchantment id = entry.getKey();
					int lvl = entry.getValue();
					if (EnchantmentHelper.getEnchantmentLevel(id.getEnchantmentByID(21), stack) == lvl) {
						EntityItem entityToSpawn = new EntityItem(killedEntity.world, (killedEntity.posX), (killedEntity.posY + 0.5), (killedEntity.posZ), new ItemStack(ItemsInit.CRYSTAL_DARKNESS, lvl));
						entityToSpawn.setPickupDelay(10);
						killedEntity.world.spawnEntity(entityToSpawn);
					}
				}
				if (EnchantmentHelper.getEnchantments(stack).isEmpty() == true || EnchantmentHelper.getEnchantments(stack) != Enchantments.LOOTING) {
					EntityItem entityToSpawn = new EntityItem(killedEntity.world, (killedEntity.posX), (killedEntity.posY + 0.5), (killedEntity.posZ), new ItemStack(ItemsInit.CRYSTAL_DARKNESS, 1));
					entityToSpawn.setPickupDelay(10);
					killedEntity.world.spawnEntity(entityToSpawn);
				}
			}
		return true;
	}
}
