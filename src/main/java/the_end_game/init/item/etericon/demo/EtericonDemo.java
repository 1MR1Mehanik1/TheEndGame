package the_end_game.init.item.etericon.demo;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import the_end_game.Main;
import the_end_game.init.item.etericon.api.EtericonApi;
import the_end_game.init.item.etericon.book.BookElement;
import the_end_game.init.item.etericon.book.BookIcon;
import the_end_game.init.item.etericon.book.BookPage;
import the_end_game.init.item.etericon.book.UnlockShape;

public final class EtericonDemo {
    private EtericonDemo() {
    }

    public static void register() {
        ResourceLocation basics = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "basics");
        ResourceLocation entry = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "etericon");
        ResourceLocation howToUse = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "how_to_use");
        ResourceLocation etericonTexture = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/item/etericon.png");
        BookIcon pageImage = new BookIcon.TextureIcon(etericonTexture, 128, 128, 40, 40);
        BookPage available = new BookPage();
        available.left(
                BookElement.title(Component.translatable("etericon.the_end_game.entry.etericon")),
                BookElement.text(Component.translatable("etericon.the_end_game.entry.etericon.available.text")),
                BookElement.link(Component.translatable("etericon.the_end_game.entry.etericon.link"), howToUse)
        );
        available.right(
                BookElement.image(pageImage),
                BookElement.text(Component.translatable("etericon.the_end_game.entry.etericon.right.text"))
        );
        BookPage opened = new BookPage();
        opened.left(
                BookElement.title(Component.translatable("etericon.the_end_game.entry.etericon")),
                BookElement.text(Component.translatable("etericon.the_end_game.entry.etericon.opened.text1")),
                BookElement.link(Component.translatable("etericon.the_end_game.entry.etericon.link"), howToUse)
        );
        opened.right(
                BookElement.image(pageImage),
                BookElement.textTitle(
                        Component.translatable("etericon.the_end_game.entry.etericon.right.title"),
                        Component.translatable("etericon.the_end_game.entry.etericon.right.text")
                )
        );
        BookPage openedSecond = new BookPage();
        openedSecond.left(
                BookElement.title(Component.translatable("etericon.the_end_game.entry.etericon.opened.title2")),
                BookElement.text(Component.translatable("etericon.the_end_game.entry.etericon.opened.text2"))
        );
        EtericonApi.entry(entry)
                .chapter(basics)
                .title(Component.translatable("etericon.the_end_game.entry.etericon"))
                .tooltip(Component.translatable("etericon.the_end_game.entry.etericon.tooltip"))
                .shape(UnlockShape.CIRCLE)
                .pos(120.0F, 92.0F)
                .startUnlocked()
                .icon(new BookIcon.ItemIcon(new ItemStack(Items.WRITABLE_BOOK), 16, 16))
                .availablePages(available)
                .openedPages(opened, openedSecond)
                .register();
    }
}
