package the_end_game.init.item.etericon.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import the_end_game.init.item.etericon.book.BookEntry;

public final class EtericonToast implements Toast {
    private static final long DURATION = 5000L;
    private final BookEntry entry;

    private EtericonToast(BookEntry entry) {
        this.entry = entry;
    }

    public static void show(ResourceLocation id) {
        BookEntry entry = ClientEtericonData.catalog().entry(id);
        if (entry == null) { return; }
        Minecraft.getInstance().getToasts().addToast(new EtericonToast(entry));
    }

    @Override public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        int width = width();
        int height = height();
        graphics.fill(0, 0, width, height, 0xF0100818);
        graphics.fill(0, 0, width, 1, 0xFF4A8FB8);
        graphics.fill(0, height - 1, width, height, 0xFF4A8FB8);
        EtericonAtlas.drawIcon(graphics, this.entry.icon(), 8, 8, 16, 16);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, Component.translatable("gui.the_end_game.etericon.notice"), 30, 6, 0xFF7FD4FF, false);
        String title = font.plainSubstrByWidth(this.entry.title().getString(), 122);
        graphics.drawString(font, Component.literal(title), 30, 18, 0xFFF0E6D2, false);
        return timeSinceLastVisible >= DURATION ? Visibility.HIDE : Visibility.SHOW;
    }
}
