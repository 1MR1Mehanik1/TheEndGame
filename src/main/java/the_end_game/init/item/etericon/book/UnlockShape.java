package the_end_game.init.item.etericon.book;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum UnlockShape implements StringRepresentable {
    CIRCLE("circle", 0),
    SQUARE("square", 1),
    PENTAGON("pentagon", 2),
    STAR4("star4", 3),
    STAR8("star8", 4);

    public static final Codec<UnlockShape> CODEC = StringRepresentable.fromEnum(UnlockShape::values);

    private final String name;
    private final int row;

    UnlockShape(String name, int row) {
        this.name = name;
        this.row = row;
    }

    @Override public String getSerializedName() { return this.name; }

    public int row() { return this.row; }

    public static UnlockShape byName(String name) {
        for (UnlockShape shape : values()) {
            if (shape.name.equals(name)) { return shape; }
        }
        return CIRCLE;
    }

    public static UnlockShape forUnlock(UnlockCondition condition) {
        if (condition instanceof UnlockCondition.Research) { return CIRCLE; }
        if (condition instanceof UnlockCondition.Theory) { return SQUARE; }
        if (condition instanceof UnlockCondition.Craft) { return STAR4; }
        if (condition instanceof UnlockCondition.Study) { return PENTAGON; }
        return STAR8;
    }
}
