package the_end_game.init.item.etericon.multiblock;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public final class Matcher {
    private final BlockState defaultState;
    private final Predicate<BlockState> predicate;
    private final BlockState exactState;
    private final boolean wildcard;

    private Matcher(BlockState defaultState, Predicate<BlockState> predicate, BlockState exactState, boolean wildcard) {
        this.defaultState = defaultState;
        this.predicate = predicate;
        this.exactState = exactState;
        this.wildcard = wildcard;
    }

    public static Matcher wildcard() {
        return new Matcher(Blocks.AIR.defaultBlockState(), state -> true, null, true);
    }
    public static Matcher block(Block block) { return new Matcher(block.defaultBlockState(), state -> state.is(block), null, false); }
    public static Matcher state(BlockState state) {
        return new Matcher(state, state::equals, state, false);
    }
    public static Matcher tag(TagKey<Block> tag) { return new Matcher(Blocks.AIR.defaultBlockState(), state -> state.is(tag), null, false); }

    public boolean matches(BlockState state) {
        return this.wildcard || this.predicate.test(state);
    }
    public boolean isWildcard() { return this.wildcard; }
    public BlockState getDefaultState() { return this.defaultState; }

    public Matcher rotate(Rotation rotation) {
        BlockState state = this.exactState == null ? this.defaultState : this.exactState;
        BlockState rotated = state.rotate(rotation);
        if (this.exactState != null) { return state(rotated); }
        return new Matcher(rotated, this.predicate, null, this.wildcard);
    }

    public Matcher mirror(Mirror mirror) {
        BlockState state = this.exactState == null ? this.defaultState : this.exactState;
        BlockState mirrored = state.mirror(mirror);
        if (this.exactState != null) { return state(mirrored); }
        return new Matcher(mirrored, this.predicate, null, this.wildcard);
    }
}
