package the_end_game.init.item.etericon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import the_end_game.init.item.etericon.network.EtericonNetwork;

import java.util.List;

public class Etericon extends Item {
    public Etericon(Properties properties) {
        super(properties);
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) { EtericonNetwork.open(serverPlayer); }
        return InteractionResultHolder.success(stack);
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !context.getLevel().isClientSide && player instanceof ServerPlayer serverPlayer) { EtericonNetwork.open(serverPlayer); }
        return InteractionResult.SUCCESS;
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.the_end_game.etericon.info"));
    }
}
