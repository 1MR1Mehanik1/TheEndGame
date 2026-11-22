package the_end_game.init.item.etericon.book;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum EntryState implements StringRepresentable {
    LOCKED("locked"),
    AVAILABLE("available"),
    OPENED("opened");

    public static final Codec<EntryState> CODEC = StringRepresentable.fromEnum(EntryState::values);

    private final String name;

    EntryState(String name) { this.name = name; }

    @Override public String getSerializedName() { return this.name; }

    public static EntryState byName(String name) {
        for (EntryState state : values()) {
            if (state.name.equals(name)) { return state; }
        }
        return LOCKED;
    }
}
