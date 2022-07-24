package the_end_game.init;

import net.minecraft.util.*;
import net.minecraftforge.fml.common.*;
import the_end_game.*;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class DamageInit extends DamageSource {
	public DamageInit(String damageType) { super(damageType); }
	
	public static DamageSource VOID = new DamageSource("void_damage").setDamageBypassesArmor().setDamageIsAbsolute();
}
