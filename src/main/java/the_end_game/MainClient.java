package the_end_game;

import gcapi_model_animation.GCApiInitClient;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import the_end_game.init.InitBlock;
import the_end_game.init.InitMenu;
import the_end_game.init.block.table.*;
import the_end_game.init.block.table_theories.TableTheoriesItemRenderer;
import the_end_game.init.block.table_theories.TableTheoriesScreen;

@Mod(value = Main.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT)
public class MainClient {
    public MainClient(ModContainer container) {}

    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent e) { e.enqueueWork(GCApiInitClient::init); }

    @SubscribeEvent public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers e) { InitBlock.regRenderBE(e); }

    @SubscribeEvent public static void onRegisterMenuScreens(RegisterMenuScreensEvent e) {
        e.register(InitMenu.TABLE_THEORIES.get(), TableTheoriesScreen::new);
    }

    @SubscribeEvent public static void onRegisterClientExtensions(RegisterClientExtensionsEvent e) {
        e.registerItem(new IClientItemExtensions() { @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return TableItemRenderer.INSTANCE; } }, InitBlock.TABLE.get().asItem());
        e.registerItem(new IClientItemExtensions() { @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return TableTheoriesItemRenderer.INSTANCE; } }, InitBlock.TABLE_THEORIES.get().asItem());
    }
}
