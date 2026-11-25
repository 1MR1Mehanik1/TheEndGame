package the_end_game.init.item.etericon.client;

import the_end_game.init.item.etericon.book.BookCatalog;
import the_end_game.init.item.etericon.book.BookProgress;

public final class ClientEtericonData {
    private static BookCatalog catalog = BookCatalog.empty();
    private static BookProgress progress = new BookProgress();
    private static String version = "";
    private static int research;

    public static BookCatalog catalog() { return catalog; }
    public static BookProgress progress() { return progress; }
    public static String version() { return version; }
    public static int research() { return research; }

    public static void set(String versionValue, BookCatalog value, BookProgress progressValue, int researchValue) {
        version = versionValue;
        catalog = value;
        progress = progressValue;
        research = researchValue;
    }

    public static void setCatalog(String versionValue, BookCatalog value) {
        version = versionValue;
        catalog = value;
    }

    public static void setProgress(BookProgress value, int researchValue) {
        progress = value;
        research = researchValue;
    }
}
