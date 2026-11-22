package the_end_game.handler.vanilla_3x3;

import net.minecraft.network.chat.Component;

public interface CraftBlockState {
    boolean isBlocked();
    boolean hasTable();
    Component missingBlock();
    Component info();
    void setBlocked(boolean blocked, Component missing, Component info);
    void updateBlocked();
}
