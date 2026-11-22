package the_end_game.init.item.etericon.client.scene.map;

import net.minecraft.client.gui.GuiGraphics;
import the_end_game.init.item.etericon.book.EntryState;

public final class CurveRenderer {
    private static final int STEPS = 48;

    private CurveRenderer() {
    }

    public static void draw(GuiGraphics graphics, int x1, int y1, int x2, int y2, EntryState state1, EntryState state2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length < 0.001) { return; }
        int color;
        int edgeColor;
        double amplitude;
        double waves;
        double speed;
        double radius;
        double edgeRadius;
        boolean bothOpened = state1 == EntryState.OPENED && state2 == EntryState.OPENED;
        boolean openedAndAvailable = (state1 == EntryState.OPENED && state2 == EntryState.AVAILABLE) || (state1 == EntryState.AVAILABLE && state2 == EntryState.OPENED);
        if (bothOpened) {
            color = 0x402B0202;
            edgeColor = 0x05FF0000;
            amplitude = 2.6;
            waves = 2.2;
            speed = 0.0022;
        } else if (openedAndAvailable) {
            color = 0x40614848;
            edgeColor = 0x05FF0000;
            amplitude = 2.2;
            waves = 2.0;
            speed = 0.0011;
        } else {
            color = 0x706E6E6E;
            edgeColor = 0x306E6E6E;
            amplitude = 0.0;
            waves = 0.0;
            speed = 0.0;
        }
        double normalX = -dy / length;
        double normalY = dx / length;
        double baseArc = Math.min(7.0, length * 0.06);
        double phase = System.currentTimeMillis() * speed;
        int previousX = 0;
        int previousY = 0;
        for (int index = 0; index <= STEPS; index++) {
            double t = index / (double) STEPS;
            double envelope = Math.sin(Math.PI * t);
            double wave = Math.sin(t * Math.PI * 2.0 * waves - phase) * amplitude * envelope;
            double offset = envelope * baseArc + wave;
            int x = (int) Math.round(x1 + dx * t + normalX * offset);
            int y = (int) Math.round(y1 + dy * t + normalY * offset);
            if (index > 0) { drawSegment(graphics, previousX, previousY, x, y, color, edgeColor); }
            previousX = x;
            previousY = y;
        }
    }

    private static void drawSegment(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color, int edgeColor) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        graphics.fill(minX - 1, minY, maxX + 1, maxY + 1, edgeColor);
        graphics.fill(minX, minY, maxX + 1, maxY + 1, color);
    }
}
