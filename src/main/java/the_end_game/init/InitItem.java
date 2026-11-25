package the_end_game.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import the_end_game.Main;
import the_end_game.init.item.*;
import the_end_game.init.item.etericon.Etericon;

import java.util.function.UnaryOperator;

public class InitItem {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Main.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Main.MOD_ID);

    //=============[Data]=============
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DATA_INK = registerData("ink", b -> b.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    //=============[Item]=============
    public static final DeferredItem<Item> ETERICON_WET = ITEMS.register("etericon_wet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Etericon> ETERICON = ITEMS.register("etericon", () -> new Etericon(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Inkwell> INKWELL = ITEMS.register("inkwell", () -> new Inkwell(new Item.Properties().stacksTo(1).component(DATA_INK.get(), 40), new ItemStack(Items.GLASS_BOTTLE, 1)));
    public static final DeferredItem<WritingFeather> WRITING_FEATHER = ITEMS.register("writing_feather", () -> new WritingFeather(new Item.Properties().stacksTo(1).durability(20)));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerData(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) { return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build()); }
    public static Iterable<Item> getItems() { return ITEMS.getEntries().stream().map(Holder::value).toList(); }
    public static void register(IEventBus e) {
        ITEMS.register(e);
        DATA_COMPONENTS.register(e);
    }
}