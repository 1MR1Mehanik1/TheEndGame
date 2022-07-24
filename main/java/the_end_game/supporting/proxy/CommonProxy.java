package the_end_game.supporting.proxy;

import net.minecraft.client.model.*;
import net.minecraft.item.*;
import net.minecraft.item.ItemArmor.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.event.*;
import the_end_game.supporting.events.*;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e) {}
	public void init(FMLInitializationEvent e) {}
	public void postInit(FMLPostInitializationEvent e) {}
	
	public void registerItemRenderer(Item item, int meta, String id) {}
	
	public ModelBiped getArmourModel(ArmorMaterial material) { return null; }
	
	public World getClientWorld() { return null; }
	
	public void registerEvent() {
	    MinecraftForge.EVENT_BUS.register(new EntityHandEvent());
	    MinecraftForge.EVENT_BUS.register(new ItemHandEvent());
	}
}
