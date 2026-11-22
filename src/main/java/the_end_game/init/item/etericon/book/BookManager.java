package the_end_game.init.item.etericon.book;

import the_end_game.init.item.etericon.api.EtericonApi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class BookManager {
    private static volatile BookCatalog catalog;
    private static volatile String version = "0";

    public static void set(BookCatalog value) {
        catalog = value;
        version = hash(BookJson.catalogToJson(value));
    }

    public static BookCatalog get() { return catalog; }

    public static String version() {
        BookCatalog current = catalog;
        return current == null ? hash(BookJson.catalogToJson(EtericonApi.javaCatalog())) : version;
    }

    public static BookCatalog getOrJavaOnly() {
        BookCatalog current = catalog;
        return current == null ? EtericonApi.javaCatalog() : current;
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(bytes.length * 2);
            for (byte current : bytes) { result.append(String.format("%02x", current)); }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            return Integer.toHexString(value.hashCode());
        }
    }
}
