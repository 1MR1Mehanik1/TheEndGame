package the_end_game.supporting.events;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Main.MODID, value = Side.CLIENT)
public class CustomTextureStitchHandler {
	@SubscribeEvent
    public static void pre(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        map.registerSprite(new ResourceLocation(Main.MODID, "othes/particles/particle_blood"));
        
        map.registerSprite(new ResourceLocation(Main.MODID + ":othes/gui/empty_slot_amulet"));
        map.registerSprite(new ResourceLocation(Main.MODID + ":othes/gui/empty_slot_bracer"));
        map.registerSprite(new ResourceLocation(Main.MODID + ":othes/gui/empty_slot_ring"));
        map.registerSprite(new ResourceLocation(Main.MODID + ":othes/gui/empty_slot_charm_health"));
        map.registerSprite(new ResourceLocation(Main.MODID + ":othes/gui/empty_slot_charm_armor"));
    }
}
