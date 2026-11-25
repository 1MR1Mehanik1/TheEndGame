package the_end_game.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import the_end_game.init.InitItem;

import java.util.List;

public class Inkwell extends Item {
    private final ItemStack emptyForm;

    public Inkwell(Properties properties, ItemStack emptyForm) {
        super(properties);
        this.emptyForm = emptyForm;
    }

    public int getInk(ItemStack stack) { return stack.getOrDefault(InitItem.DATA_INK.get(), 0); }
    public int getMaxInk(ItemStack stack) {
        Integer max = stack.getItem().components().get(InitItem.DATA_INK.get());
        return max != null ? max : 1;
    }

    public void consumeInk(Level level, Player player, ItemStack stack, InteractionHand hand, int amount) {
        if (amount <= 0) return;

        int remaining = getInk(stack) - amount;
        if (remaining <= 0) {
            player.setItemInHand(hand, emptyForm.copy());
            level.playSound(null, player.blockPosition(), SoundEvents.DECORATED_POT_INSERT, SoundSource.PLAYERS, 0.8F, 0.8F);
        } else { stack.set(InitItem.DATA_INK.get(), remaining); }
    }

    @Override public boolean isBarVisible(ItemStack stack) { return getInk(stack) < getMaxInk(stack); }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13.0F * getInk(stack) / getMaxInk(stack)); }
    @Override public int getBarColor(ItemStack stack) { return 0x7B93E3; }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.the_end_game.inkwell.ink", getInk(stack), getMaxInk(stack)).withStyle(ChatFormatting.BLUE));
    }
}
