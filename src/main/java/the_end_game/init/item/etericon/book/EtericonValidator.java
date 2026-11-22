package the_end_game.init.item.etericon.book;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class EtericonValidator {
    public enum Severity {
        WARNING,
        ERROR
    }

    public record Issue(Severity severity, String message) {
        @Override public String toString() { return this.severity.name() + ": " + this.message; }
    }

    public static List<Issue> validate(BookCatalog catalog) {
        List<Issue> issues = new ArrayList<>();
        Set<ResourceLocation> chaptersWithEntries = new HashSet<>();
        for (BookEntry entry : catalog.entries().values()) {
            chaptersWithEntries.add(entry.chapter());
            if (catalog.chapter(entry.chapter()) == null) {
                issues.add(new Issue(Severity.ERROR, "Entry " + entry.id() + " references missing chapter " + entry.chapter()));
            }
            if (entry.x() < 0.0F || entry.x() > 272.0F || entry.y() < 0.0F || entry.y() > 180.0F) {
                issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " is outside map bounds: " + entry.x() + ", " + entry.y()));
            }
            for (ResourceLocation required : entry.requires()) {
                if (required.equals(entry.id())) {
                    issues.add(new Issue(Severity.ERROR, "Entry " + entry.id() + " requires itself"));
                } else if (catalog.entry(required) == null) {
                    issues.add(new Issue(Severity.ERROR, "Entry " + entry.id() + " requires missing entry " + required));
                }
            }
            for (UnlockCondition condition : entry.unlocks()) {
                if (condition instanceof UnlockCondition.Research research && research.points() <= 0) {
                    issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " has non-positive research cost"));
                }
                if (condition instanceof UnlockCondition.Kill kill && kill.count() <= 0) {
                    issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " has non-positive kill count"));
                }
            }
            if (entry.visibility() == EntryVisibility.UNTIL_OPENED && !entry.startUnlocked()) {
                issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " is hidden until opened but cannot be opened from the map"));
            }
            if (entry.visibility() == EntryVisibility.HIDDEN_UNTIL && entry.hiddenUntil().isEmpty()) {
                issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " uses hidden_until visibility without hidden_until conditions"));
            }
            if (entry.visibility() != EntryVisibility.HIDDEN_UNTIL && !entry.hiddenUntil().isEmpty()) {
                issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " has hidden_until conditions ignored by visibility " + entry.visibility().getSerializedName()));
            }
            for (int stageIndex = 0; stageIndex < entry.stages().size(); stageIndex++) {
                List<UnlockCondition> stage = entry.stages().get(stageIndex);
                if (stage.isEmpty()) {
                    issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " has empty stage " + (stageIndex + 1)));
                }
                for (UnlockCondition condition : stage) {
                    if (condition instanceof UnlockCondition.Research research && research.points() <= 0) {
                        issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " stage " + (stageIndex + 1) + " has non-positive research cost"));
                    }
                    if (condition instanceof UnlockCondition.Kill kill && kill.count() <= 0) {
                        issues.add(new Issue(Severity.WARNING, "Entry " + entry.id() + " stage " + (stageIndex + 1) + " has non-positive kill count"));
                    }
                }
            }
        }
        for (BookChapter chapter : catalog.chapters().values()) {
            if (!chaptersWithEntries.contains(chapter.id())) {
                issues.add(new Issue(Severity.WARNING, "Chapter " + chapter.id() + " has no entries"));
            }
        }
        detectCycles(catalog, issues);
        return issues;
    }

    private static void detectCycles(BookCatalog catalog, List<Issue> issues) {
        Map<ResourceLocation, Integer> marks = new HashMap<>();
        for (ResourceLocation id : catalog.entries().keySet()) {
            if (marks.getOrDefault(id, 0) == 0) {
                visit(id, catalog, marks, issues);
            }
        }
    }

    private static void visit(ResourceLocation id, BookCatalog catalog, Map<ResourceLocation, Integer> marks, List<Issue> issues) {
        marks.put(id, 1);
        BookEntry entry = catalog.entry(id);
        if (entry != null) {
            for (ResourceLocation required : entry.requires()) {
                int mark = marks.getOrDefault(required, 0);
                if (mark == 1) {
                    issues.add(new Issue(Severity.ERROR, "Requires cycle detected at " + id + " -> " + required));
                } else if (mark == 0 && catalog.entry(required) != null) {
                    visit(required, catalog, marks, issues);
                }
            }
        }
        marks.put(id, 2);
    }

    public static long errorCount(List<Issue> issues) {
        return issues.stream().filter(issue -> issue.severity() == Severity.ERROR).count();
    }

    public static long warningCount(List<Issue> issues) {
        return issues.stream().filter(issue -> issue.severity() == Severity.WARNING).count();
    }
}
