package the_end_game.init.item.etericon.book;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum EntryVisibility implements StringRepresentable {
    ALWAYS("always"),
    UNTIL_OPENED("until_opened"),
    UNTIL_REQUIREMENTS("until_requirements"),
    HIDDEN_UNTIL("hidden_until");

    public static final Codec<EntryVisibility> CODEC = StringRepresentable.fromEnum(EntryVisibility::values);

    private final String name;

    EntryVisibility(String name) { this.name = name; }

    @Override public String getSerializedName() { return this.name; }

    public static EntryVisibility byName(String name) {
        for (EntryVisibility visibility : values()) {
            if (visibility.name.equals(name)) { return visibility; }
        }
        return ALWAYS;
    }
}
