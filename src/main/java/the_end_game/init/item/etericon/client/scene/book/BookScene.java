package the_end_game.init.item.etericon.client.scene.book;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import the_end_game.init.item.etericon.book.BookContent;
import the_end_game.init.item.etericon.book.BookElement;
import the_end_game.init.item.etericon.book.BookEntry;
import the_end_game.init.item.etericon.book.BookPage;
import the_end_game.init.item.etericon.book.BookProgress;
import the_end_game.init.item.etericon.book.EntryAccess;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.book.UnlockCondition;
import the_end_game.init.item.etericon.client.ClientEtericonData;
import the_end_game.init.item.etericon.client.EtericonAtlas;
import the_end_game.init.item.etericon.client.EtericonScreen;

import java.util.ArrayList;
import java.util.List;

public final class BookScene {
    private static final int LEFT_X = 16;
    private static final int RIGHT_X = 140;
    private static final int PAGE_Y = 12;
    private static final int PAGE_WIDTH = 116;
    private static final int PAGE_HEIGHT = 152;

    private final EtericonScreen screen;
    private int spread;

    public BookScene(EtericonScreen screen) {
        this.screen = screen;
    }

    public int spread() { return this.spread; }
    public void setSpread(int value) { this.spread = value; }
    public void clampSpread(int size) { this.spread = Math.max(0, Math.min(Math.max(0, size - 1), this.spread)); }

    public void draw(GuiGraphics graphics) {
        EtericonAtlas.drawBook(graphics);
        BookEntry entry = this.screen.entryId() == null ? null : ClientEtericonData.catalog().entry(this.screen.entryId());
        if (entry == null) { return; }
        List<BookPage> pages = this.displayPages(entry);
        if (pages.isEmpty()) {
            this.screen.drawCentered(graphics, Component.translatable("gui.the_end_game.etericon.empty_page"), 70, 82, 0.7F, 0xFF7A5A3A);
            this.screen.drawCentered(graphics, Component.translatable("gui.the_end_game.etericon.empty_page"), 200, 82, 0.7F, 0xFF7A5A3A);
            if (!this.screen.overlayBase()) {
                this.screen.drawBackButton(graphics, 2, this.screen::backToMap);
                this.drawStageState(graphics, entry, pages);
            }
            return;
        }
        this.clampSpread(pages.size());
        BookPage page = pages.get(this.spread);
        this.drawPage(graphics, page.left(), LEFT_X, PAGE_Y, PAGE_WIDTH, PAGE_HEIGHT);
        this.drawPage(graphics, page.right(), RIGHT_X, PAGE_Y, PAGE_WIDTH, PAGE_HEIGHT);
//        graphics.fill(LEFT_X, PAGE_Y, LEFT_X + PAGE_WIDTH, PAGE_Y + PAGE_HEIGHT, 0x30FF0000);
//        graphics.fill(RIGHT_X, PAGE_Y, RIGHT_X + PAGE_WIDTH, PAGE_Y + PAGE_HEIGHT, 0x300000FF);
        if (this.screen.overlayBase()) { return; }
        this.screen.drawBackButton(graphics, 2, this.screen::backToMap);
        if (this.screen.entryState() == EntryState.AVAILABLE && !entry.hasStages()) {
            int x = 252;
            int y = 2;
            boolean hovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), x, y, 16, 16);
            if (hovered) {
                graphics.fill(x, y, x + 16, y + 16, 0x22FFFFFF);
                this.screen.setHoveredButtonTooltip(List.of(Component.translatable("gui.the_end_game.etericon.study")));
            }
            EtericonAtlas.drawEye(graphics, x + 3, y + 5, hovered);
            this.screen.addClickable(x, y, 16, 16, this.screen::confirmEntry);
        }
        this.drawStageState(graphics, entry, pages);
        if (this.spread > 0) {
            int x = 2;
            int y = 166;
            boolean hovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), x, y, 18, 12);
            if (hovered) {
                graphics.fill(x, y, x + 18, y + 12, 0x22FFFFFF);
                this.screen.setHoveredButtonTooltip(List.of(Component.translatable("gui.the_end_game.etericon.previous_page")));
            }
            EtericonAtlas.drawArrowLeft(graphics, x, y, hovered, true);
            this.screen.addClickable(x, y, 18, 12, () -> this.spread--);
        }
        if (this.spread < pages.size() - 1) {
            int x = 252;
            int y = 166;
            boolean hovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), x, y, 18, 12);
            if (hovered) {
                graphics.fill(x, y, x + 18, y + 12, 0x22FFFFFF);
                this.screen.setHoveredButtonTooltip(List.of(Component.translatable("gui.the_end_game.etericon.next_page")));
            }
            EtericonAtlas.drawArrowRight(graphics, x, y, hovered, true);
            this.screen.addClickable(x, y, 18, 12, () -> this.spread++);
        }
    }

    private void drawStageState(GuiGraphics graphics, BookEntry entry, List<BookPage> pages) {
        if (!entry.hasStages() || this.screen.entryState() != EntryState.AVAILABLE) { return; }
        if (!pages.isEmpty() && this.spread < pages.size() - 1) { return; }
        BookProgress progress = ClientEtericonData.progress();
        int stage = EntryAccess.stageIndex(entry, progress);
        List<UnlockCondition> current = EntryAccess.currentStage(entry, progress);
        if (current.isEmpty()) { return; }
        BookPage page = pages.isEmpty() ? null : pages.get(this.spread);
        boolean rightPage = page == null || !page.right().isEmpty() || page.left().isEmpty();
        int pageX = rightPage ? RIGHT_X : LEFT_X;
        if (stage > 0) {
            this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.stage", stage + 1, entry.stages().size()), pageX, 170, 0.45F, 0xFF5A3A1A, false);
        }
        int cost = EntryAccess.researchCost(current);
        if (cost <= 0) { return; }
        int buttonX = pageX + PAGE_WIDTH - 13;
        int buttonY = 167;
        boolean hovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), buttonX, buttonY, 13, 13);
        boolean available = EntryAccess.stageUnlocked(current, progress) && progress.researchPoints() >= cost;
        if (!available) { RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.4F); }
        EtericonAtlas.drawEye(graphics, buttonX + 1, buttonY + 3, hovered);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (hovered) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.the_end_game.etericon.stage.pay", cost));
            if (!EntryAccess.stageUnlocked(current, progress)) {
                lines.add(Component.translatable("gui.the_end_game.etericon.stage.blocked"));
            }
            if (progress.researchPoints() < cost) {
                lines.add(Component.translatable("gui.the_end_game.etericon.stage.missing", progress.researchPoints(), cost));
            }
            this.screen.setHoveredButtonTooltip(lines);
        }
        this.screen.addClickable(buttonX, buttonY, 13, 13, this.screen::confirmEntry);
    }

    public List<BookPage> displayPages(BookEntry entry) {
        List<BookPage> source = entry.pagesFor(this.screen.entryState());
        List<BookPage> result = new ArrayList<>();
        for (BookPage page : source) {
            List<List<BookElement>> left = this.paginateElements(page.left(), PAGE_WIDTH);
            List<List<BookElement>> right = this.paginateElements(page.right(), PAGE_WIDTH);
            int count = Math.max(left.size(), right.size());
            for (int index = 0; index < count; index++) {
                BookPage virtual = new BookPage();
                if (index < left.size()) { virtual.left(left.get(index).toArray(BookElement[]::new)); }
                if (index < right.size()) { virtual.right(right.get(index).toArray(BookElement[]::new)); }
                result.add(virtual);
            }
        }
        return result;
    }

    private List<List<BookElement>> paginateElements(List<BookElement> elements, int width) {
        List<List<BookElement>> pages = new ArrayList<>();
        List<BookElement> queue = new ArrayList<>();
        for (BookElement element : elements) {
            int x = element.x() == null ? 0 : element.x();
            int elementWidth = element.width() == null ? Math.max(1, width - x) : Math.min(element.width(), Math.max(1, width - x));
            int height = element.height() == null ? this.measureContent(element.content(), elementWidth) : element.height();
            if (height > PAGE_HEIGHT && element.content() instanceof BookContent.Text text) {
                queue.addAll(this.splitTextElement(element, text, elementWidth));
            } else {
                queue.add(element);
            }
        }
        List<BookElement> current = new ArrayList<>();
        int cursor = 0;
        for (BookElement element : queue) {
            int x = element.x() == null ? 0 : element.x();
            int elementWidth = element.width() == null ? Math.max(1, width - x) : Math.min(element.width(), Math.max(1, width - x));
            int height = element.height() == null ? this.measureContent(element.content(), elementWidth) : element.height();
            int y = element.y() == null ? cursor : element.y();
            if (y + height > PAGE_HEIGHT && !current.isEmpty()) {
                pages.add(current);
                current = new ArrayList<>();
                cursor = 0;
                y = element.y() == null ? 0 : element.y();
            }
            BookElement copy = this.copyElement(element, x, y);
            current.add(copy);
            cursor = Math.max(cursor, y + height + element.gap());
        }
        if (!current.isEmpty() || pages.isEmpty()) { pages.add(current); }
        return pages;
    }

    private List<BookElement> splitTextElement(BookElement element, BookContent.Text text, int width) {
        List<BookElement> result = new ArrayList<>();
        int scale = Math.max(6, Math.round(9.0F * text.scale()));
        int maxLines = Math.max(1, (PAGE_HEIGHT - 1) / scale);
        int wrapWidth = Math.max(8, (int) (width / text.scale()));
        List<String> lines = this.wrapPlainText(text.text().getString(), wrapWidth);
        for (int start = 0; start < lines.size(); start += maxLines) {
            int end = Math.min(lines.size(), start + maxLines);
            String chunk = String.join(" ", lines.subList(start, end));
            BookElement copy = new BookElement(new BookContent.Text(Component.literal(chunk).withStyle(text.text().getStyle()), text.scale(), text.color(), text.shadow())).gap(element.gap());
            if (element.width() != null) { copy.width(element.width()); }
            result.add(copy);
        }
        return result;
    }

    private List<String> wrapPlainText(String text, int wrapWidth) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\\n", -1)) {
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (this.screen.font().width(candidate) <= wrapWidth) {
                    line.setLength(0);
                    line.append(candidate);
                } else {
                    if (!line.isEmpty()) { lines.add(line.toString()); }
                    line.setLength(0);
                    line.append(word);
                }
            }
            lines.add(line.toString());
        }
        return lines;
    }

    private BookElement copyElement(BookElement element, int x, int y) {
        BookElement copy = new BookElement(element.content()).at(x, y).gap(element.gap());
        if (element.width() != null) { copy.width(element.width()); }
        if (element.height() != null) { copy.height(element.height()); }
        return copy;
    }

    private int measureContent(BookContent content, int width) {
        if (content instanceof BookContent.Title title) {
            return Math.max(10, Math.round(9.0F * title.scale()) + 4);
        }
        if (content instanceof BookContent.Text text) {
            return this.measureText(text.text(), width, text.scale());
        }
        if (content instanceof BookContent.TextTitle textTitle) {
            int titleHeight = Math.round(9.0F * 0.75F) + 3;
            return titleHeight + this.measureText(textTitle.text(), width, textTitle.scale());
        }
        if (content instanceof BookContent.Image image) {
            return image.icon().height() + (image.caption().getString().isEmpty() ? 0 : 10);
        }
        if (content instanceof BookContent.ImageText imageText) {
            int imageWidth = Math.min(imageText.imageWidth(), Math.max(8, width - 20));
            int imageHeight = Math.max(8, Math.round(imageText.icon().height() * imageWidth / (float) Math.max(1, imageText.icon().width())));
            int textWidth = Math.max(8, width - imageWidth - 4);
            int result = Math.max(imageHeight, this.measureText(imageText.text(), textWidth, imageText.scale()));
            return result + (imageText.caption().getString().isEmpty() ? 0 : 10);
        }
        if (content instanceof BookContent.Container container) {
            int cursor = 0;
            int cross = 0;
            int count = container.children().size();
            for (int index = 0; index < count; index++) {
                BookElement child = container.children().get(index);
                int remaining = count - index;
                int childWidth = child.width() != null ? Math.min(child.width(), width) : container.horizontal()
                        ? Math.max(1, (width - cursor - container.gap() * Math.max(0, remaining - 1)) / remaining)
                        : width;
                int childHeight = child.height() == null ? this.measureContent(child.content(), childWidth) : child.height();
                if (container.horizontal()) {
                    cursor += childWidth + container.gap();
                    cross = Math.max(cross, childHeight);
                } else {
                    cursor += childHeight + child.gap();
                    cross = Math.max(cross, childWidth);
                }
            }
            return container.horizontal() ? cross : Math.max(0, cursor - container.gap());
        }
        if (content instanceof BookContent.Crafting crafting) {
            return 58 + (crafting.caption().getString().isEmpty() ? 0 : 9);
        }
        if (content instanceof BookContent.RecipeLink recipe) {
            return 58 + (recipe.caption().getString().isEmpty() ? 0 : 9);
        }
        if (content instanceof BookContent.Entity entity) {
            return 64 + (entity.caption().getString().isEmpty() ? 0 : 10);
        }
        if (content instanceof BookContent.Item item) {
            return Math.max(12, Math.round(16.0F * item.scale())) + (item.caption().getString().isEmpty() ? 0 : 9);
        }
        if (content instanceof BookContent.Link) { return 9; }
        return 0;
    }

    private int measureText(Component text, int width, float scale) {
        int wrapWidth = Math.max(8, (int) (width / scale));
        List<FormattedCharSequence> lines = this.screen.font().split(text, wrapWidth);
        return lines.isEmpty() ? 0 : lines.size() * Math.max(6, Math.round(9.0F * scale)) + 1;
    }

    private void drawPage(GuiGraphics graphics, List<BookElement> elements, int x, int y, int width, int height) {
        if (elements.isEmpty()) { return; }
        this.screen.enableScissor(graphics, x, y, width, height);
        int cursor = 0;
        for (BookElement element : elements) {
            int elementX = element.x() == null ? 0 : element.x();
            int elementY = element.y() == null ? cursor : element.y();
            int availableWidth = Math.max(1, width - elementX);
            int elementHeight = this.screen.drawContent(graphics, element.content(), x + elementX, y + elementY, availableWidth, element.width(), element.height());
            cursor = Math.max(cursor, elementY + elementHeight + element.gap());
        }
        this.screen.disableScissor(graphics);
    }
}
