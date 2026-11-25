package the_end_game.init.item.etericon.book;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class EntryAccess {
    public static boolean isVisible(BookEntry entry, BookProgress progress) {
        return switch (entry.visibility()) {
            case ALWAYS -> true;
            case UNTIL_OPENED -> progress.stateOf(entry) == EntryState.OPENED;
            case UNTIL_REQUIREMENTS -> {
                if (entry.requires().isEmpty()) { yield true; }
                for (ResourceLocation required : entry.requires()) {
                    if (progress.stateOf(required) != EntryState.OPENED) { yield false; }
                }
                yield true;
            }
            case HIDDEN_UNTIL -> {
                for (UnlockCondition condition : entry.hiddenUntil()) {
                    if (!condition.isSatisfied(progress)) { yield false; }
                }
                yield true;
            }
        };
    }

    public static boolean requirementsMet(BookEntry entry, BookProgress progress) {
        for (ResourceLocation required : entry.requires()) {
            if (progress.stateOf(required) != EntryState.OPENED) { return false; }
        }
        return true;
    }

    public static boolean isAvailable(BookEntry entry, BookProgress progress) {
        if (entry.startUnlocked()) { return true; }
        if (!requirementsMet(entry, progress)) { return false; }
        if (entry.unlocks().isEmpty()) { return true; }
        for (UnlockCondition condition : entry.unlocks()) {
            if (!(condition instanceof UnlockCondition.Research) && condition.isSatisfied(progress)) { return true; }
        }
        return false;
    }

    public static boolean stageReady(List<UnlockCondition> conditions, BookProgress progress) {
        for (UnlockCondition condition : conditions) {
            if (!condition.isSatisfied(progress)) { return false; }
        }
        return true;
    }

    public static boolean stageUnlocked(List<UnlockCondition> conditions, BookProgress progress) {
        for (UnlockCondition condition : conditions) {
            if (condition instanceof UnlockCondition.Research) { continue; }
            if (!condition.isSatisfied(progress)) { return false; }
        }
        return true;
    }

    public static int researchCost(List<UnlockCondition> conditions) {
        int cost = 0;
        for (UnlockCondition condition : conditions) {
            if (condition instanceof UnlockCondition.Research research) { cost += research.points(); }
        }
        return cost;
    }

    public static int stageIndex(BookEntry entry, BookProgress progress) {
        return Math.max(0, Math.min(entry.stages().size(), progress.stageOf(entry.id())));
    }

    public static List<UnlockCondition> currentStage(BookEntry entry, BookProgress progress) {
        int index = stageIndex(entry, progress);
        if (index >= entry.stages().size()) { return List.of(); }
        return entry.stages().get(index);
    }

    public static UnlockCondition.Research findResearch(BookEntry entry, BookProgress progress, int research) {
        if (!requirementsMet(entry, progress)) { return null; }
        for (UnlockCondition condition : entry.unlocks()) {
            if (condition instanceof UnlockCondition.Research cost && research >= cost.points()) { return cost; }
        }
        return null;
    }

    public static boolean canPayStage(BookEntry entry, BookProgress progress, int research) {
        List<UnlockCondition> stage = currentStage(entry, progress);
        if (stage.isEmpty()) { return false; }
        int cost = researchCost(stage);
        if (cost <= 0) { return false; }
        return stageUnlocked(stage, progress) && research >= cost;
    }

    public static int chapterTotal(BookChapter chapter, BookCatalog catalog) {
        return catalog.entriesFor(chapter.id()).size();
    }

    public static int chapterOpened(BookChapter chapter, BookCatalog catalog, BookProgress progress) {
        int opened = 0;
        for (BookEntry entry : catalog.entriesFor(chapter.id())) {
            if (progress.stateOf(entry) == EntryState.OPENED) { opened++; }
        }
        return opened;
    }

    public static int chapterPercent(BookChapter chapter, BookCatalog catalog, BookProgress progress) {
        int total = chapterTotal(chapter, catalog);
        if (total <= 0) { return 0; }
        return Math.round(chapterOpened(chapter, catalog, progress) * 100.0F / total);
    }

    public static float chapterFraction(BookChapter chapter, BookCatalog catalog, BookProgress progress) {
        int total = chapterTotal(chapter, catalog);
        if (total <= 0) { return 0.0F; }
        return chapterOpened(chapter, catalog, progress) / (float) total;
    }
}
