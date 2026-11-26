package the_end_game.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import the_end_game.init.InitItem;

import java.util.List;

public class TheoryItem extends Item {
    public TheoryItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation id = stack.get(InitItem.DATA_THEORY_ID.get());
        if (id != null) {
            tooltip.add(Component.translatable("item.the_end_game.theory.id", id.toString()).withStyle(ChatFormatting.GRAY));
        }
    }
}
