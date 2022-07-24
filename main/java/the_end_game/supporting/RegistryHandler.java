package the_end_game.supporting;

import net.minecraft.block.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.eventhandler.*;
import the_end_game.init.*;

@EventBusSubscriber
public class RegistryHandler {
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) { event.getRegistry().registerAll(ItemsInit.ITEMS.toArray(new Item[0])); }
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) { event.getRegistry().registerAll(BlocksInit.BLOCKS.toArray(new Block[0])); }
	@SubscribeEvent 
	public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) { event.getRegistry().registerAll(EnchantmentsInit.ENCHANTMENTS.toArray(new Enchantment[0])); }
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		for(Item item : ItemsInit.ITEMS) if(item instanceof IHasModel) ((IHasModel)item).registerModels();
		for(Block block : BlocksInit.BLOCKS) if(block instanceof IHasModel) ((IHasModel)block).registerModels();
	}
}
