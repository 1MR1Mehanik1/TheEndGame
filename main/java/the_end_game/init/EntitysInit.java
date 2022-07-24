package the_end_game.init;

import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.registry.*;
import the_end_game.*;
import the_end_game.entitys.*;

public class EntitysInit {
	public static final int BLOOD_BALL = 124000;
	public static final int MIRROR_VESSEL = 124001;
	
	public static void registerEntitys() {
		regEntity_no_egg("blood_ball", EntityBloodBall.class, BLOOD_BALL, 8);
		regEntity_no_egg("mirror_vessel_grave", EntityMirrorVessel.class, MIRROR_VESSEL, 32);
	}
	
	private static void regEntity_egg(String name, Class<? extends Entity> entity, int id, int trackingRange, int color1, int color2) {
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, name), entity, name, id, Main.instance, trackingRange, 1, true, color1, color2);
	}
	private static void regEntity_no_egg(String name, Class<? extends Entity> entity, int id, int trackingRange) {
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, name), entity, name, id, Main.instance, trackingRange, 1, true);
	}
}
