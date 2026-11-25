package the_end_game.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class PlayerVariables {
    public static final int DEFAULT_RESEARCH = 0;
    public static final int DEFAULT_MIND = 100;
    public static final int DEFAULT_FRENZY = 0;

    public static final Codec<PlayerVariables> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("research", DEFAULT_RESEARCH).forGetter(PlayerVariables::research),
            Codec.INT.optionalFieldOf("mind", DEFAULT_MIND).forGetter(PlayerVariables::mind),
            Codec.INT.optionalFieldOf("frenzy", DEFAULT_FRENZY).forGetter(PlayerVariables::frenzy)
    ).apply(instance, PlayerVariables::new));

    private int research;
    private int mind;
    private int frenzy;

    public PlayerVariables() { this(DEFAULT_RESEARCH, DEFAULT_MIND, DEFAULT_FRENZY); }

    public PlayerVariables(int research, int mind, int frenzy) {
        this.research = research;
        this.mind = mind;
        this.frenzy = frenzy;
    }

    public int research() { return this.research; }

    public int mind() { return this.mind; }

    public int frenzy() { return this.frenzy; }

    public void setResearch(int value) { this.research = value; }

    public void setMind(int value) { this.mind = value; }

    public void setFrenzy(int value) { this.frenzy = value; }
}
