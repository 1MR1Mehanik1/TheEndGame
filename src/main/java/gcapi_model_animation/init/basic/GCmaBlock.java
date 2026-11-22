package gcapi_model_animation.init.basic;

import gcapi_model_animation.api.IModeledObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GCmaBlock extends Block implements EntityBlock, IModeledObject {
    private final Supplier<? extends BlockEntityType<?>> eType;
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animModel;

    public GCmaBlock(Properties props, Supplier<? extends BlockEntityType<?>> eType, ResourceLocation model, ResourceLocation texture) {
        this(props, eType, model, texture, null);
    }
    public GCmaBlock(Properties props, Supplier<? extends BlockEntityType<?>> eType, ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animModel) {
        super(props);
        this.eType = eType;
        this.model = model;
        this.texture = texture;
        this.animModel = animModel;
    }

    @Override public ResourceLocation getGeometryLocation() { return model; }
    @Override public ResourceLocation getTextureLocation() { return texture; }
    @Nullable public ResourceLocation getAnimationFile() { return animModel; }

    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GCmaBlockEntity(eType.get(), pos, state);
    }
    public BlockEntityType<?> getBlockEntityType() { return eType.get(); }

    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, st, be) -> {
            if (be instanceof GCmaBlockEntity basic) basic.tick(lvl);
        };
    }
}
