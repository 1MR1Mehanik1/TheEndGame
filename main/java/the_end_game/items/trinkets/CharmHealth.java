package the_end_game.items.trinkets;

import java.util.*;

import javax.annotation.*;

import net.minecraft.client.util.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import the_end_game.Main;
import the_end_game.items.*;
import the_end_game.supporting.*;
import the_end_game.trinket_inventory.*;

public class CharmHealth extends BasicTrinketsItem {
	public static final String LEVEL_1 = "health_level_1";
	public static final String LEVEL_2 = "health_level_2";
	public static final String LEVEL_3 = "health_level_3";
	
	public CharmHealth(String name, int maxStackSize) {
		super(name, maxStackSize);
		addPropertyOverride(new ResourceLocation(Main.MODID, "level_1"), (stack, world, entity) -> isLevel_1(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "level_2"), (stack, world, entity) -> isLevel_2(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation(Main.MODID, "level_3"), (stack, world, entity) -> isLevel_3(stack) ? 1 : 0);
	}
	
	@Override public TrinketType getTrinketType(ItemStack itemstack) { return TrinketType.CHARM_HEALTH; }
	
	public static boolean isLevel_1(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, LEVEL_1, false); }
	public static boolean isLevel_2(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, LEVEL_2, false); }
	public static boolean isLevel_3(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, LEVEL_3, false); }
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		if (this.isLevel_1(stack)) list.add(I18n.translateToLocalFormatted("trinket.description.charm_health_1"));
		if (this.isLevel_2(stack)) list.add(I18n.translateToLocalFormatted("trinket.description.charm_health_2"));
		if (this.isLevel_3(stack)) list.add(I18n.translateToLocalFormatted("trinket.description.charm_health_3"));
	}
	
	//in creative inventori
	@Override
    public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tabs)) {
            ItemStack stack_1 = new ItemStack(this);
            if (!this.isLevel_1(stack_1)) {
            	ItemNBTHelper.setBoolean(stack_1, LEVEL_1, true);
                list.add(stack_1);
            }
            if (!this.isLevel_2(stack_1)) {
            	ItemStack stack_2 = new ItemStack(this);
            	ItemNBTHelper.setBoolean(stack_2, LEVEL_2, true);
                list.add(stack_2);
            }
            if (!this.isLevel_3(stack_1)) {
            	ItemStack stack_3 = new ItemStack(this);
            	ItemNBTHelper.setBoolean(stack_3, LEVEL_3, true);
                list.add(stack_3);
            }
        }
    }
}
