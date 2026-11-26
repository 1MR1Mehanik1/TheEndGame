package the_end_game.init.research;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import the_end_game.init.InitItem;

public final class ResearchInteraction {
    private ResearchInteraction() {
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!canPerform(event.getEntity())) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (event.getLevel().isClientSide()) return;
        ResearchRegistry.Research research = ResearchRegistry.block(event.getLevel().getBlockState(event.getPos()).getBlock());
        perform(event.getEntity(), event.getLevel(), research);
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!canPerform(event.getEntity())) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (event.getLevel().isClientSide()) return;
        ResearchRegistry.Research research = ResearchRegistry.entity(event.getTarget().getType());
        perform(event.getEntity(), event.getLevel(), research);
    }

    private static boolean canPerform(Player player) {
        return !player.isSpectator() && featherHand(player) != null && paperHand(player) != null && findInkwell(player) >= 0;
    }

    private static void perform(Player player, Level level, ResearchRegistry.Research research) {
        InteractionHand featherHand = featherHand(player);
        InteractionHand paperHand = paperHand(player);
        int inkwellSlot = findInkwell(player);
        if (featherHand == null || paperHand == null || inkwellSlot < 0) return;

        ItemStack feather = player.getItemInHand(featherHand);
        ItemStack paper = player.getItemInHand(paperHand);
        ItemStack inkwell = player.getInventory().getItem(inkwellSlot);

        paper.shrink(1);
        damage(feather, player);
        damage(inkwell, player);
        player.getInventory().setChanged();

        if (research == null || level.random.nextFloat() >= research.chance()) return;

        ItemStack theory = new ItemStack(InitItem.THEORY.get());
        theory.set(InitItem.DATA_THEORY_ID.get(), research.id());
        if (!player.getInventory().add(theory)) player.drop(theory, false);
    }

    private static void damage(ItemStack stack, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        stack.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer, item -> { });
    }

    private static InteractionHand featherHand(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            if (player.getItemInHand(hand).is(InitItem.WRITING_FEATHER.get())) return hand;
        }
        return null;
    }

    private static InteractionHand paperHand(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            if (player.getItemInHand(hand).is(Items.PAPER)) return hand;
        }
        return null;
    }

    private static int findInkwell(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).is(InitItem.INKWELL.get())) return slot;
        }
        return -1;
    }
}
