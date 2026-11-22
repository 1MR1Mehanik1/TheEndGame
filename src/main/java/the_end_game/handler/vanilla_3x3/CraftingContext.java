package the_end_game.handler.vanilla_3x3;

public final class CraftingContext {
    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    public static void enter() { DEPTH.set(DEPTH.get() + 1); }
    public static void exit() {
        int depth = DEPTH.get() - 1;
        if (depth <= 0) { DEPTH.remove(); } else { DEPTH.set(depth); }
    }
    public static boolean active() { return DEPTH.get() > 0; }
}
