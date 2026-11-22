package the_end_game.init;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import the_end_game.Main;
import the_end_game.init.item.etericon.Etericon;

import java.util.function.UnaryOperator;

public class InitItem {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Main.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Main.MOD_ID);

    public static final DeferredItem<Item> ETERICON_WET = ITEMS.register("etericon_wet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Etericon> ETERICON = ITEMS.register("etericon", () -> new Etericon(new Item.Properties().stacksTo(1)));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerData(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) { return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build()); }
    public static Iterable<Item> getItems() { return ITEMS.getEntries().stream().map(Holder::value).toList(); }
    public static void register(IEventBus e) {
        ITEMS.register(e);
        DATA_COMPONENTS.register(e);
    }
}