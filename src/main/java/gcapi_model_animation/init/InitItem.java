package gcapi_model_animation.init;

//import gcapi_model_animation.init.basic.BasicItem;
//import net.minecraft.core.Holder;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Item;
//import net.neoforged.neoforge.registries.DeferredItem;
//import net.neoforged.neoforge.registries.DeferredRegister;
//import vanilla_relict_battle.Main;
//
//import java.util.ArrayList;
//import java.util.List;

//public class InitItem {
//    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Main.MOD_ID);
//
//    public static final DeferredItem<BasicItem> LTKATANA = InitItem.ITEMS.registerItem("ltkatana",
//            p -> new BasicItem(p)
//                    .setModel(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID,"models/item/ltkatana.geo.json"))
//                    .setTexture(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/item/ltkatana.png")));
//    public static final DeferredItem<BasicItem> TEST_ITEM = InitItem.ITEMS.registerItem("test_item",
//            p -> new BasicItem(p).setModel(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID,"models/item/test_item.geo.json")));
//
//    public static Iterable<Item> getItems() {
//        return ITEMS.getEntries().stream().map(Holder::value).toList();
//    }
//}
