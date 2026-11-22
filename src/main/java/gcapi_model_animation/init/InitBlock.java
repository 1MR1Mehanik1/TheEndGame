package gcapi_model_animation.init;

//import gcapi_model_animation.init.basic.BasicBlock;
//import gcapi_model_animation.init.basic.BasicBlockEntity;
//import gcapi_model_animation.init.basic.BasicBlockItem;
//import net.minecraft.core.Holder;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.BlockItem;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.SoundType;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.material.MapColor;
//import net.neoforged.neoforge.registries.DeferredBlock;
//import net.neoforged.neoforge.registries.DeferredHolder;
//import net.neoforged.neoforge.registries.DeferredRegister;
//import vanilla_relict_battle.Main;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.BiFunction;
//import java.util.function.Supplier;
//
//public class InitBlock {
//    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Main.MOD_ID);
//    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Main.MOD_ID);
//
//    public static final List<Block> BLOCKS_LIST = new ArrayList<>();
//
//    // === Блок ===
//    public static final DeferredBlock<BasicBlock> TEST_BLOCK = registerBlock("test_block", () -> new BasicBlock(Block.Properties.of()
//            .mapColor(MapColor.STONE)
//            .strength(1.0f)
//            .sound(SoundType.STONE))
//            .setModel(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "models/block/test_block.geo.json"))
//            .setAnimation(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "models/block/test_block.animation.json"))
//    );
//
//    // === BlockEntityType (после блока, ссылается на TEST_BLOCK) ===
//    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasicBlockEntity>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("test_block",
//            () -> BlockEntityType.Builder
//                    .of(BasicBlockEntity::new, TEST_BLOCK.get())
//                    .build(null));
//
//    public static Iterable<Block> getBlocks() {
//        List<Block> result = new ArrayList<>();
//        for (DeferredHolder<Block, ? extends Block> holder : BLOCKS.getEntries()) { result.add(holder.value()); }
//        return result;
//    }
//    public static <B extends Block> DeferredBlock<B> registerBlock(String name, Supplier<B> blockFactory) {
//        return registerBlock(name, blockFactory, BasicBlockItem::new);
//    }
//    public static <B extends Block> DeferredBlock<B> registerBlock(String name, Supplier<B> blockFactory, BiFunction<Block, Item.Properties, ? extends BlockItem> itemFactory) {
//        DeferredBlock<B> block = BLOCKS.register(name, blockFactory);
//        InitItem.ITEMS.registerItem(name, props -> itemFactory.apply(block.get(), props));
//        return block;
//    }
//}
