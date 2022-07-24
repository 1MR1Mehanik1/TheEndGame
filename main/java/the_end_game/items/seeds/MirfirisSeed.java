package the_end_game.items.seeds;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.*;
import the_end_game.Main;
import the_end_game.init.BlocksInit;
import the_end_game.init.ItemsInit;
import the_end_game.supporting.*;

public class MirfirisSeed extends Item implements IHasModel, IPlantable {
	public MirfirisSeed(String name,int maxStackSize) {
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
		if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, stack) && state.getBlock() == Blocks.SOUL_SAND && world.isAirBlock(pos.up())) {
			world.setBlockState(pos.up(), BlocksInit.MIRFIRIS_PLANT.getDefaultState());
			stack.shrink(1);
			return EnumActionResult.SUCCESS;
		} else return EnumActionResult.FAIL;
	}
}
