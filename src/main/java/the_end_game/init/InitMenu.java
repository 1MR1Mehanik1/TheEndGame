package the_end_game.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import the_end_game.Main;
import the_end_game.init.block.table_theories.TableTheoriesMenu;

public class InitMenu {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Main.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<TableTheoriesMenu>> TABLE_THEORIES =
            MENUS.register("table_theories", () -> new MenuType<>(TableTheoriesMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus bus) { MENUS.register(bus); }
}
