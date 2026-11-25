package the_end_game.init.block.table_theories;

import gcapi_model_animation.init.basic.GCmaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class TableTheories extends GCmaBlock {
    public TableTheories(Properties properties, Supplier<? extends BlockEntityType<?>> type, ResourceLocation model, ResourceLocation texture) {
        super(properties, type, model, texture);
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new TableTheoriesEntity(getBlockEntityType(), pos, state); }
}
