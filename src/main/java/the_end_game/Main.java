package the_end_game;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import the_end_game.handler.vanilla_3x3.Vanilla3x3Events;
import the_end_game.init.*;
import the_end_game.init.block.table.Table;

@Mod(Main.MOD_ID)
public class Main {
    public static final String MOD_ID = "the_end_game";

    public Main(IEventBus modBus, ModContainer container) {
        InitCreativeTabs.register(modBus);
        InitItem.register(modBus);
        InitEtericon.register(modBus);
        InitPlayerVariables.register(modBus);
        InitBlock.register(modBus);

        Vanilla3x3Events.register(modBus);
        addEventBus();
    }

    private static void addEventBus() {
        NeoForge.EVENT_BUS.addListener(Table::onRightClickBlock);
    }
}