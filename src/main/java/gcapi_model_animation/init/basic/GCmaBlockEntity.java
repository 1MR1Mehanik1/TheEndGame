package gcapi_model_animation.init.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GCmaBlockEntity extends BlockEntity {
    public final AnimationState animState = new AnimationState();

    public GCmaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Вызывается как на клиенте, так и на сервере (через Block#getTicker) */
    public void tick(Level level) {
        // здесь можно останавливать анимацию по времени на сервере,
        // но рендер сам обрабатывает окончание не-луп анимаций.
    }

    public void playAnimation(String name, boolean loop, float speed) {
        animState.isPlaying = true;
        animState.currentAnimation = name;
        animState.startTime = System.currentTimeMillis();
        animState.speed = speed;
        animState.loop = loop;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void stopAnimation() {
        animState.isPlaying = false;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // === NBT ===
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("AnimPlaying", animState.isPlaying);
        if (animState.currentAnimation != null) tag.putString("AnimName", animState.currentAnimation);
        tag.putLong("AnimStart", animState.startTime);
        tag.putFloat("AnimSpeed", animState.speed);
        tag.putBoolean("AnimLoop", animState.loop);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        animState.isPlaying = tag.getBoolean("AnimPlaying");
        animState.currentAnimation = tag.contains("AnimName") ? tag.getString("AnimName") : null;
        animState.startTime = tag.getLong("AnimStart");
        animState.speed = tag.contains("AnimSpeed") ? tag.getFloat("AnimSpeed") : 1f;
        animState.loop = !tag.contains("AnimLoop") || tag.getBoolean("AnimLoop");
    }

    public static class AnimationState {
        public boolean isPlaying = false;
        public String currentAnimation = null;
        public long startTime = 0L;
        public float speed = 1f;
        public boolean loop = true;
    }
}