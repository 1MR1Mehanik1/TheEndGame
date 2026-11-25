package the_end_game.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import the_end_game.Main;

import java.util.function.Supplier;

public class InitCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MOD_ID);

    public static final Supplier<CreativeModeTab> TEG_TAB = CREATIVE_MOD_TABS.register("teg_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(InitItem.ETERICON.get())).title(Component.translatable("creative_tab." + Main.MOD_ID))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(InitItem.ETERICON_WET);
                        output.accept(InitItem.ETERICON);

                        //=============[Item]=============
                        output.accept(InitItem.INKWELL);
                        output.accept(InitItem.WRITING_FEATHER);

                        //=============[Block]=============
                        output.accept(InitBlock.TABLE);
                    })).build());

    public static void register(IEventBus e) { CREATIVE_MOD_TABS.register(e); }
}
