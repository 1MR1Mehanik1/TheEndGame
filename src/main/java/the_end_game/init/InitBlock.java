package the_end_game.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import the_end_game.Main;
import the_end_game.init.block.table.Table;
import the_end_game.init.block.table.TableEntity;
import the_end_game.init.block.table.TableRenderer;

import java.util.function.Supplier;

public class InitBlock {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Main.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Main.MOD_ID);

    public static final DeferredBlock<Table> TABLE = registerBlock("table", () -> new Table(
            BlockBehaviour.Properties.of().strength(3.0F, 3.0F).noOcclusion().sound(SoundType.WOOD).dynamicShape(),
            InitBlock.TABLE_BE,
            ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "models/block/table.geo.json"),
            ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/block/table/table.png")));
    private static BlockEntityType<TableEntity> createTableBE() { return BlockEntityType.Builder.of((pos, state) -> new TableEntity(TABLE_BE.get(), pos, state), TABLE.get()).build(null); }
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TableEntity>> TABLE_BE = BLOCK_ENTITIES.register("table", InitBlock::createTableBE);


    public static void regRenderBE(EntityRenderersEvent.RegisterRenderers e) {
        e.registerBlockEntityRenderer(TABLE_BE.get(), TableRenderer::new);
    }
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> regBlockReturn = BLOCKS.register(name, block);
        registerBlockItem(name, regBlockReturn);
        return regBlockReturn;
    }
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        InitItem.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    public static Iterable<Block> getBlocks() { return BLOCKS.getEntries().stream().map(Holder::value).toList(); }
    public static void register(IEventBus e) {
        BLOCKS.register(e);
        BLOCK_ENTITIES.register(e);
    }
}
