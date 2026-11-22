package the_end_game.handler.vanilla_3x3;

import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BelowData {
    public record Requirement(BlockPredicate predicate, String state, boolean nbt, Component info) {}
    private static volatile Map<ResourceLocation, Requirement> required = Map.of();
    public static void set(Map<ResourceLocation, Requirement> data) { required = Map.copyOf(data); }
    public static Requirement requirement(RecipeHolder<?> recipe) { return recipe == null ? null : required.get(recipe.id()); }

    public static boolean matches(Level level, BlockPos tablePos, RecipeHolder<?> recipe) { return matches(level, tablePos, requirement(recipe)); }
    public static boolean matches(Level level, BlockPos tablePos, Requirement requirement) {
        if (requirement == null) { return true; }
        if (tablePos == null) { return false; }
        return level instanceof ServerLevel serverLevel && requirement.predicate().matches(serverLevel, tablePos.below());
    }

    public static Component missing(Requirement requirement) {
        Component blocks = blockNames(requirement.predicate());
        boolean state = !requirement.state().isEmpty();
        if (blocks == null) {
            if (state) { return Component.translatable("gui.the_end_game.crafting.below.any_state", requirement.state()); }
            return Component.translatable("gui.the_end_game.crafting.below.any");
        }
        if (state && requirement.nbt()) { return Component.translatable("gui.the_end_game.crafting.below.missing_state_nbt", blocks, requirement.state()); }
        if (state) { return Component.translatable("gui.the_end_game.crafting.below.missing_state", blocks, requirement.state()); }
        if (requirement.nbt()) { return Component.translatable("gui.the_end_game.crafting.below.missing_nbt", blocks); }
        return Component.translatable("gui.the_end_game.crafting.below.missing", blocks);
    }

    private static Component blockNames(BlockPredicate predicate) {
        Optional<HolderSet<Block>> optional = predicate.blocks();
        if (optional.isEmpty()) { return null; }
        Either<TagKey<Block>, List<Holder<Block>>> unwrapped = optional.get().unwrap();
        if (unwrapped.left().isPresent()) { return Component.literal("#" + unwrapped.left().get().location()); }
        List<Holder<Block>> holders = unwrapped.right().orElse(List.of());
        if (holders.isEmpty()) { return null; }
        List<Component> names = new ArrayList<>();
        for (Holder<Block> holder : holders) {
            names.add(blockName(holder.value()));
            if (names.size() >= 3) { break; }
        }
        MutableComponent result = Component.empty();
        for (int index = 0; index < names.size(); index++) {
            if (index > 0) { result.append(", "); }
            result.append(names.get(index));
        }
        if (holders.size() > 3) { result.append("…"); }
        return result;
    }

    private static Component blockName(Block block) {
        Item item = block.asItem();
        if (item == Items.AIR) { return Component.literal(BuiltInRegistries.BLOCK.getKey(block).toString()); }
        return new ItemStack(item).getHoverName();
    }
}
