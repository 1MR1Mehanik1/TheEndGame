package the_end_game;

import net.minecraft.creativetab.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.*;
import the_end_game.init.*;
import the_end_game.supporting.*;
import the_end_game.supporting.network.*;
import the_end_game.supporting.proxy.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION, acceptedMinecraftVersions = Main.MC_VERSION)
public class Main {
	public static final String MODID = "the_end_game";
	public static final String NAME = "The End Game";
	public static final String VERSION = "0.3";
	public static final String MC_VERSION = "[1.12.2]";
	
	public static final String CLIENT = "the_end_game.supporting.proxy.ClientProxy";
	public static final String SERVER = "the_end_game.supporting.proxy.CommonProxy";
	
	public static final CreativeTabs TEG = new CreativeTabs(Main.MODID) { @Override public ItemStack getTabIconItem() { return new ItemStack(ItemsInit.SOUL_SHARD); } };

	@Instance
	public static Main instance;
	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;
	
	@EventHandler public static void preInit(FMLPreInitializationEvent e) {
		PotionsInit.registerPotions();
		EntitysInit.registerEntitys();
		RegAndRenderHandler.registerEntityRenders();
		
		CapabilityManager.INSTANCE.register(ITrinketItemHandler.class, new CAPTrinket.CapabilityTrinket<ITrinketItemHandler>(), CAPTrinketStorage.class);
		CapabilityManager.INSTANCE.register(ITrinket.class, new CAPTrinket.CapabilityItemTrinketStorage(), () -> new TrinketItem(TrinketType.NORMAL));
		PacketHandler.init();
		proxy.registerEvent();
		proxy.preInit(e);
	}
	@EventHandler public static void init(FMLInitializationEvent e) {
		RegCrafts.init();
		RegAndRenderHandler.initEvent();
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
		
		KeyHandler.register();
		
		proxy.init(e);
	}
	@EventHandler public static void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}
}
