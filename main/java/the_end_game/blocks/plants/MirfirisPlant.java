package the_end_game.blocks.plants;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import the_end_game.*;
import the_end_game.init.*;
import the_end_game.supporting.*;

public class MirfirisPlant extends BlockBush implements IHasModel, IGrowable {
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
    private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[] {
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.25D, 0.35D),
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.25D, 0.35D),
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.25D, 0.35D),
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.25D, 0.35D),
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.4D, 0.35D),
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.4D, 0.35D),
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.7D, 0.35D),
    	new AxisAlignedBB(0.65D, 0.0D, 0.65D, 0.35D, 0.7D, 0.35D)
    };
    
    public MirfirisPlant(final String name, final Material material, float hardness, float resistanse, String hravLevel, int level, SoundType soundtype) {
    	super(material);
		this.setRegistryName(name);
		this.setUnlocalizedName(name);
		this.setSoundType(soundtype);
		this.setHardness(hardness);
		this.setResistance(resistanse);
		this.setHarvestLevel(hravLevel, level);
		
		setCreativeTab(null);
		
		BlocksInit.BLOCKS.add(this);
		ItemsInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
    @Override public void registerModels() { Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory"); }
    
    protected Item getSeed() { return ItemsInit.MIRFIRIS_SEED; }
    protected Item getCrop() { return ItemsInit.MIRFIRIS_FRUIT; }
    
    public int getMaxAge() { return 7; }
    protected boolean canSustainBush(IBlockState state) { return state.getBlock() == Blocks.SOUL_SAND; }
    
    public static PropertyInteger getAgeProperty() { return AGE; }
    public static int getAge(IBlockState state) { return state.getValue(getAgeProperty()).intValue(); }
    public IBlockState withAge(int age) { return this.getDefaultState().withProperty(getAgeProperty(), Integer.valueOf(age)); }
    public boolean isMaxAge(IBlockState state) { return ((Integer)state.getValue(this.getAgeProperty())).intValue() >= this.getMaxAge(); }
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return CROPS_AABB[((Integer)state.getValue(this.getAgeProperty())).intValue()]; }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) { return this.isMaxAge(state) ? this.getCrop() : this.getSeed(); }
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) { return new ItemStack(this.getSeed()); }
    public IBlockState getStateFromMeta(int meta) { return this.withAge(meta); }
    public int getMetaFromState(IBlockState state) { return this.getAge(state); }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, new IProperty[] {AGE}); }
    
    protected int getBonemealAgeIncrease(World world) { return MathHelper.getInt(world.rand, 0, 1); }
    @Override public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) { return true; }
    @Override public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) { return !this.isMaxAge(state); }
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) { this.grow(worldIn, pos, state); }
    
    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        super.getDrops(drops, world, pos, state, 0);
        int age = getAge(state);
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        if (age >= getMaxAge()) {
            drops.add(new ItemStack(this.getSeed(), rand.nextInt(3), 0));
        }
    }
    
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState soil = worldIn.getBlockState(pos.down());
        return (worldIn.canSeeSky(pos)) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
    }
    public void grow(World world, BlockPos pos, IBlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(world);
        int j = this.getMaxAge();
        if (i > j) i = j;
        world.setBlockState(pos, this.withAge(i), 2);
    }
    
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        int i = this.getAge(state);
        int range = 3;
        if (!world.isAreaLoaded(pos, 1)) return;
        if (world.isDaytime()) if (i < this.getMaxAge()) {
        	if (rand.nextInt(20) == 0) world.setBlockState(pos, this.withAge(i + 1), 2);
        }
    }
}
