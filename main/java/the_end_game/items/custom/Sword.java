package the_end_game.items.custom;

import java.util.*;
import java.util.Map.*;

import javax.annotation.*;

import net.minecraft.client.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class Sword extends ItemSword implements IHasModel {
	public Sword(String name, ToolMaterial material, float damage, float speed) {
		super(material);
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
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		list.add(I18n.translateToLocalFormatted("title.text.not_working"));
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
		if (killedEntity instanceof EntityAnimal || killedEntity instanceof EntityAmbientCreature) {
			World world = killedEntity.world;
			if (killedEntity != null && killedEntity.isEntityAlive() == false && world.isDaytime() && Math.random() < 0.1) if (!killedEntity.world.isRemote) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
				for (Entry<Enchantment, Integer> entry : map.entrySet()) {
					Enchantment id = entry.getKey();
					int lvl = entry.getValue();
					if(EnchantmentHelper.getEnchantmentLevel(id.getEnchantmentByID(21), stack) == lvl) {
						EntityItem entityToSpawn = new EntityItem(killedEntity.world, (killedEntity.posX), (killedEntity.posY + 0.5), (killedEntity.posZ), new ItemStack(ItemsInit.CRYSTAL_LIGHT, lvl));
						entityToSpawn.setPickupDelay(10);
						killedEntity.world.spawnEntity(entityToSpawn);
					}
				}
				if(EnchantmentHelper.getEnchantments(stack).isEmpty() == true || EnchantmentHelper.getEnchantments(stack) != Enchantments.LOOTING) {
					EntityItem entityToSpawn = new EntityItem(killedEntity.world, (killedEntity.posX), (killedEntity.posY + 0.5), (killedEntity.posZ), new ItemStack(ItemsInit.CRYSTAL_LIGHT, 1));
					entityToSpawn.setPickupDelay(10);
					killedEntity.world.spawnEntity(entityToSpawn);
				}
			}
        }
		return true;
	}
}
