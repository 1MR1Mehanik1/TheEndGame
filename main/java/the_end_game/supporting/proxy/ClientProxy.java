package the_end_game.supporting.proxy;

import java.util.*;

import net.minecraft.client.model.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.item.*;
import net.minecraft.item.ItemArmor.*;
import net.minecraft.world.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.client.*;
import net.minecraftforge.fml.common.event.*;
import the_end_game.init.*;
import the_end_game.items.armor.model.*;
import the_end_game.supporting.events.*;

public class ClientProxy extends CommonProxy {
	@Override public World getClientWorld() { return FMLClientHandler.instance().getClient().world; }
	
	public void registerItemRenderer(Item item, int meta, String id) { ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id)); }
	
	public static final Map armour_models;
	public ModelBiped getArmourModel(ArmorMaterial material) { return (ModelBiped) armour_models.get(material); }
	static { armour_models = new HashMap(); ClientProxy.armour_models.put(ItemsInit.ArmorM_2, new NoArmor()); }
	
	public void registerEvent() {
	    super.registerEvent();
	    MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	    MinecraftForge.EVENT_BUS.register(new GuiEvent());
	    MinecraftForge.EVENT_BUS.register(new HeartDisplayHandler());
	}
}
