package the_end_game.init.item.etericon.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public final class EtericonTooltip {
    public static void draw(GuiGraphics graphics, Font font, int mouseX, int mouseY, List<Component> lines) {
        draw(graphics, font, mouseX, mouseY, lines, 0, EtericonAtlas.BOOK_WIDTH, EtericonAtlas.BOOK_HEIGHT, -1.0F);
    }

    public static void draw(GuiGraphics graphics, Font font, int mouseX, int mouseY, List<Component> lines, int boundsWidth, int boundsHeight) {
        draw(graphics, font, mouseX, mouseY, lines, 0, boundsWidth, boundsHeight, -1.0F);
    }

    public static void draw(GuiGraphics graphics, Font font, int mouseX, int mouseY, List<Component> lines, int boundsY, int boundsWidth, int boundsHeight) {
        draw(graphics, font, mouseX, mouseY, lines, boundsY, boundsWidth, boundsHeight, -1.0F);
    }

    public static void draw(GuiGraphics graphics, Font font, int mouseX, int mouseY, List<Component> lines, int boundsWidth, int boundsHeight, float progress) {
        draw(graphics, font, mouseX, mouseY, lines, 0, boundsWidth, boundsHeight, progress);
    }

    public static void draw(GuiGraphics graphics, Font font, int mouseX, int mouseY, List<Component> lines, int boundsY, int boundsWidth, int boundsHeight, float progress) {
        if (lines.isEmpty()) { return; }
        int maxWidth = 112;
        List<Line> wrapped = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            Component component = lines.get(index);
            boolean title = index == 0;
            float scale = title ? 0.62F : 0.55F;
            int color = title ? 0xFFFFE8B0 : 0xFFE8DCC0;
            int wrapWidth = Math.max(16, (int) ((maxWidth - 8) / scale));
            for (FormattedCharSequence sequence : font.split(component, wrapWidth)) {
                wrapped.add(new Line(sequence, scale, color));
            }
        }
        int textWidth = 0;
        for (Line line : wrapped) { textWidth = Math.max(textWidth, (int) (font.width(line.text) * line.scale)); }
        int width = Math.min(maxWidth, Math.max(54, textWidth + 8));
        int height = 6;
        for (Line line : wrapped) { height += Math.max(5, Math.round(9.0F * line.scale)); }
        if (progress >= 0.0F) { height += 5; }
        height = Math.min(150, height);
        int x = mouseX + 6;
        int y = mouseY + 6;
        if (x + width > boundsWidth - 2) { x = mouseX - width - 6; }
        if (y + height > boundsY + boundsHeight - 2) { y = mouseY - height - 6; }
        x = Math.max(2, Math.min(boundsWidth - width - 2, x));
        y = Math.max(boundsY + 2, Math.min(boundsY + boundsHeight - height - 2, y));
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 400.0F);
        graphics.fill(x, y, x + width, y + height, 0xE6100A08);
        graphics.fill(x, y, x + width, y + 1, 0xCC8A6A3A);
        int lineY = y + 3;
        for (Line line : wrapped) {
            if (lineY > y + height - 3) { break; }
            graphics.pose().pushPose();
            graphics.pose().translate(x + 4, lineY, 1.0F);
            graphics.pose().scale(line.scale, line.scale, 1.0F);
            graphics.drawString(font, line.text, 0, 0, line.color, false);
            graphics.pose().popPose();
            lineY += Math.max(5, Math.round(9.0F * line.scale));
        }
        if (progress >= 0.0F && lineY <= y + height - 3) {
            int barX = x + 4;
            int barWidth = Math.max(4, width - 8);
            graphics.fill(barX, lineY + 1, barX + barWidth, lineY + 4, 0x805E4020);
            int filled = Math.round(barWidth * Math.max(0.0F, Math.min(1.0F, progress)));
            graphics.fill(barX, lineY + 1, barX + filled, lineY + 4, 0xFF6E9E4A);
        }
        graphics.pose().popPose();
    }

    private record Line(FormattedCharSequence text, float scale, int color) {}
}
