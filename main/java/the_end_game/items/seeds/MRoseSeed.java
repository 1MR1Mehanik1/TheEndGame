package the_end_game.items.seeds;

import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class MRoseSeed extends Item implements IHasModel, IPlantable {
	public MRoseSeed(String name,int maxStackSize) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.TEG);
		setMaxStackSize(maxStackSize);
		
		ItemsInit.ITEMS.add(this);
	}
	@Override public void registerModels() { Main.proxy.registerItemRenderer(this, 0, "inventory"); }
	
	@Override public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) { return EnumPlantType.Crop; }
	@Override public IBlockState getPlant(IBlockAccess world, BlockPos pos) { return Blocks.ANVIL.getDefaultState(); }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);
		if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, stack) && state.getBlock() == Blocks.END_STONE && world.isAirBlock(pos.up())) {
			world.setBlockState(pos.up(), BlocksInit.MROSE_PLANT.getDefaultState());
			stack.shrink(1);
			return EnumActionResult.SUCCESS;
		} else return EnumActionResult.FAIL;
	}
}
