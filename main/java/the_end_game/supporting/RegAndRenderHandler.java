package the_end_game.supporting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.client.registry.*;
import the_end_game.entitys.*;
import the_end_game.entitys.renders.*;
import the_end_game.supporting.events.*;

public class RegAndRenderHandler {
	public static void initEvent() {
		MinecraftForge.EVENT_BUS.register(new Event());
	}
	public static void registerEntityRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBloodBall.class, new IRenderFactory<EntityBloodBall>() { @Override public Render<? super EntityBloodBall> createRenderFor(RenderManager manager) { return new RenderBloodBall(manager); } });
		RenderingRegistry.registerEntityRenderingHandler(EntityMirrorVessel.class, new IRenderFactory<EntityMirrorVessel>() { @Override public Render<? super EntityMirrorVessel> createRenderFor(RenderManager manager) { return new RenderMirrorVessel(manager, Minecraft.getMinecraft().getRenderItem()); } });
	}
}
