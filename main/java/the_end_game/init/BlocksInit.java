package the_end_game.init;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import the_end_game.blocks.plants.*;

public class BlocksInit {
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	public static final Block MROSE_PLANT = new MRosePlant("midnight_rose_plant", Material.PLANTS, 0.51F, 2.5F, "", 0, SoundType.PLANT);
	public static final Block MIRFIRIS_PLANT = new MirfirisPlant("mirfiris_plant", Material.PLANTS, 0.51F, 2.5F, "", 0, SoundType.PLANT);
}
