package the_end_game.init.item.etericon.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public final class BookProgressSelfTest {
    private static final ResourceLocation ENTRY = ResourceLocation.fromNamespaceAndPath("test", "entry");
    private static final ResourceLocation THEORY = ResourceLocation.fromNamespaceAndPath("test", "theory");
    private static final ResourceLocation ITEM = ResourceLocation.fromNamespaceAndPath("test", "item");
    private static final ResourceLocation TARGET = ResourceLocation.fromNamespaceAndPath("test", "target");
    private static final ResourceLocation ENTITY = ResourceLocation.fromNamespaceAndPath("test", "entity");
    private static final ResourceLocation CHAPTER = ResourceLocation.fromNamespaceAndPath("test", "chapter");
    private static final ResourceLocation OTHER = ResourceLocation.fromNamespaceAndPath("test", "other");
    private static final ResourceLocation HIDDEN = ResourceLocation.fromNamespaceAndPath("test", "hidden");
    private static final ResourceLocation REQUIRES = ResourceLocation.fromNamespaceAndPath("test", "requires");
    private static final ResourceLocation STAGED = ResourceLocation.fromNamespaceAndPath("test", "staged");
    private static final ResourceLocation PAID = ResourceLocation.fromNamespaceAndPath("test", "paid");
    private static final ResourceLocation FIRST = ResourceLocation.fromNamespaceAndPath("test", "first");
    private static final ResourceLocation SECOND = ResourceLocation.fromNamespaceAndPath("test", "second");

    public static void main(String[] args) {
        BookProgress progress = new BookProgress(BookProgress.CURRENT_VERSION, Map.of(ENTRY, EntryState.LOCKED), 7, List.of(THEORY), List.of(ITEM), List.of(TARGET), Map.of(ENTITY, 3), List.of(ENTRY));
        BookProgress jsonRestored = BookProgress.fromJson(progress.toJson());
        check(jsonRestored.version() == BookProgress.CURRENT_VERSION, "json version");
        check(jsonRestored.researchPoints() == 7, "json points");
        check(jsonRestored.isForgotten(ENTRY), "json forgotten");
        check(jsonRestored.completedTheories().contains(THEORY), "json theories");
        check(jsonRestored.craftedItems().contains(ITEM), "json crafted");
        check(jsonRestored.studiedTargets().contains(TARGET), "json studied");
        check(jsonRestored.kills().get(ENTITY) == 3, "json kills");

        JsonElement encoded = BookProgress.CODEC.encodeStart(JsonOps.INSTANCE, progress).getOrThrow();
        BookProgress codecRestored = BookProgress.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();
        check(codecRestored.version() == BookProgress.CURRENT_VERSION, "codec version");
        check(codecRestored.isForgotten(ENTRY), "codec forgotten");
        check(codecRestored.researchPoints() == 7, "codec points");

        BookProgress state = new BookProgress();
        state.forget(ENTRY);
        check(state.stateOf(ENTRY) == EntryState.LOCKED, "forget state");
        check(state.isForgotten(ENTRY), "forget flag");
        state.restore(ENTRY);
        check(state.stateOf(ENTRY) == EntryState.OPENED, "restore state");
        check(!state.isForgotten(ENTRY), "restore flag");

        BookIcon icon = new BookIcon.AtlasIcon(ResourceLocation.fromNamespaceAndPath("test", "textures/test.png"), 16, 16, 0, 0, 16, 16, 16, 16);
        BookChapter chapter = new BookChapter(CHAPTER, Component.literal("Chapter"), Component.empty(), icon, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, null, true, List.of());
        BookEntry entry = new BookEntry(ENTRY, CHAPTER, Component.literal("Entry"), Component.empty(), UnlockShape.CIRCLE, 10.0F, 10.0F, List.of(OTHER), List.of(), true, EntryVisibility.ALWAYS, icon, List.of(), List.of(), List.of());
        BookEntry other = new BookEntry(OTHER, CHAPTER, Component.literal("Other"), Component.empty(), UnlockShape.CIRCLE, 20.0F, 20.0F, List.of(ENTRY), List.of(), true, EntryVisibility.ALWAYS, icon, List.of(), List.of(), List.of());
        BookCatalog catalog = new BookCatalog(Map.of(CHAPTER, chapter), Map.of(ENTRY, entry, OTHER, other), Map.of());

        List<EtericonValidator.Issue> issues = EtericonValidator.validate(catalog);
        check(EtericonValidator.errorCount(issues) > 0, "requires cycle validation");

        BookProgress forgotten = new BookProgress();
        forgotten.recalculate(catalog);
        check(forgotten.stateOf(entry) == EntryState.AVAILABLE, "start unlocked");
        forgotten.forget(ENTRY);
        forgotten.recalculate(catalog);
        check(forgotten.stateOf(entry) == EntryState.LOCKED, "forgotten recalculate");

        BookEntry hiddenEntry = new BookEntry(HIDDEN, CHAPTER, Component.literal("Hidden"), Component.empty(), UnlockShape.CIRCLE, 30.0F, 30.0F, List.of(), List.of(), true, EntryVisibility.UNTIL_OPENED, icon, List.of(), List.of(), List.of());
        BookEntry requiresEntry = new BookEntry(REQUIRES, CHAPTER, Component.literal("Requires"), Component.empty(), UnlockShape.CIRCLE, 40.0F, 40.0F, List.of(OTHER), List.of(), true, EntryVisibility.UNTIL_REQUIREMENTS, icon, List.of(), List.of(), List.of());
        BookProgress emptyProgress = new BookProgress();
        check(!hiddenEntry.isVisible(emptyProgress), "hidden before opened");
        BookProgress openedProgress = new BookProgress(Map.of(HIDDEN, EntryState.OPENED), 0, List.of(), List.of(), List.of(), Map.of());
        check(hiddenEntry.isVisible(openedProgress), "hidden after opened");
        BookProgress requiresProgress = new BookProgress(Map.of(OTHER, EntryState.OPENED), 0, List.of(), List.of(), List.of(), Map.of());
        check(requiresEntry.isVisible(requiresProgress), "requires visibility");

        BookChapter conditionalChapter = new BookChapter(CHAPTER, Component.literal("Conditional"), Component.empty(), icon, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, null, false, List.of(new UnlockCondition.Craft(ITEM)));
        BookProgress craftedProgress = new BookProgress(Map.of(), 0, List.of(), List.of(ITEM), List.of(), Map.of());
        check(conditionalChapter.isUnlocked(catalog, craftedProgress), "chapter condition");

        BookChapter entryChapter = new BookChapter(CHAPTER, Component.literal("Entry"), Component.empty(), icon, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, null, false, List.of());
        BookCatalog entryCatalog = new BookCatalog(Map.of(CHAPTER, entryChapter), Map.of(ENTRY, entry), Map.of());
        check(entryChapter.isUnlocked(entryCatalog, emptyProgress), "chapter entry visibility");

        JsonObject killJson = JsonParser.parseString("{\"type\":\"kill\",\"entity\":\"minecraft:zombie\",\"count\":2}").getAsJsonObject();
        UnlockCondition kill = UnlockCondition.fromJson(killJson);
        check(kill instanceof UnlockCondition.Kill, "kill parser");
        check(BookJson.parseColor(JsonParser.parseString("\"#80AABBCC\""), 0) == 0x80AABBCC, "color parser");

        BookEntry staged = new BookEntry(STAGED, CHAPTER, Component.literal("Staged"), Component.empty(), UnlockShape.CIRCLE, 50.0F, 50.0F,
                List.of(), List.of(), true, EntryVisibility.ALWAYS, icon, List.of(), List.of(), List.of(),
                List.of(List.of(new UnlockCondition.Craft(ITEM)), List.of(new UnlockCondition.Kill(ENTITY, 2))), List.of());
        BookCatalog stagedCatalog = new BookCatalog(Map.of(CHAPTER, chapter), Map.of(STAGED, staged), Map.of());
        BookProgress stagedProgress = new BookProgress();
        stagedProgress.markCrafted(ITEM);
        stagedProgress.recalculate(stagedCatalog);
        check(stagedProgress.stateOf(staged) == EntryState.AVAILABLE, "stage available");
        check(stagedProgress.stageOf(staged) == 1, "stage first completed");
        stagedProgress.addKill(ENTITY, 2);
        stagedProgress.recalculate(stagedCatalog);
        check(stagedProgress.stateOf(staged) == EntryState.OPENED, "stage all completed");
        check(stagedProgress.stageOf(staged) == 2, "stage index finished");

        BookEntry paid = new BookEntry(PAID, CHAPTER, Component.literal("Paid"), Component.empty(), UnlockShape.CIRCLE, 60.0F, 60.0F,
                List.of(), List.of(), true, EntryVisibility.ALWAYS, icon, List.of(), List.of(), List.of(),
                List.of(List.of(new UnlockCondition.Research(5))), List.of());
        BookCatalog paidCatalog = new BookCatalog(Map.of(CHAPTER, chapter), Map.of(PAID, paid), Map.of());
        BookProgress paidProgress = new BookProgress();
        paidProgress.addResearchPoints(5);
        paidProgress.recalculate(paidCatalog);
        check(paidProgress.stateOf(paid) == EntryState.AVAILABLE, "research stage waits");
        check(EntryAccess.canPayStage(paid, paidProgress), "research stage payable");
        check(paidProgress.spendResearchPoints(5), "research points spent");
        paidProgress.advanceStage(paid.id());
        paidProgress.recalculate(paidCatalog);
        check(paidProgress.stateOf(paid) == EntryState.OPENED, "research stage completed");

        BookEntry hiddenUntilEntry = new BookEntry(HIDDEN, CHAPTER, Component.literal("Hidden until"), Component.empty(), UnlockShape.CIRCLE, 70.0F, 70.0F,
                List.of(), List.of(), true, EntryVisibility.HIDDEN_UNTIL, icon, List.of(), List.of(), List.of(), List.of(),
                List.of(new UnlockCondition.Craft(ITEM)));
        check(!hiddenUntilEntry.isVisible(new BookProgress()), "hidden before trigger");
        check(hiddenUntilEntry.isVisible(new BookProgress(Map.of(), 0, List.of(), List.of(ITEM), List.of(), Map.of())), "hidden after trigger");

        BookEntry first = new BookEntry(FIRST, CHAPTER, Component.literal("First"), Component.empty(), UnlockShape.CIRCLE, 80.0F, 80.0F,
                List.of(), List.of(), true, EntryVisibility.ALWAYS, icon, List.of(), List.of(), List.of());
        BookEntry second = new BookEntry(SECOND, CHAPTER, Component.literal("Second"), Component.empty(), UnlockShape.CIRCLE, 90.0F, 90.0F,
                List.of(), List.of(), true, EntryVisibility.ALWAYS, icon, List.of(), List.of(), List.of());
        BookCatalog percentCatalog = new BookCatalog(Map.of(CHAPTER, chapter), Map.of(FIRST, first, SECOND, second), Map.of());
        BookProgress percentProgress = new BookProgress(Map.of(FIRST, EntryState.OPENED), 0, List.of(), List.of(), List.of(), Map.of());
        check(EntryAccess.chapterPercent(chapter, percentCatalog, percentProgress) == 50, "chapter percent");

        JsonObject stagedJson = JsonParser.parseString("{\"chapter\":\"test:chapter\",\"title\":\"Staged\",\"start_unlocked\":true,\"icon\":{\"atlas\":\"test:textures/test.png\",\"w\":16,\"h\":16,\"width\":16,\"height\":16},\"stages\":[{\"conditions\":[{\"type\":\"craft\",\"item\":\"test:item\"}]},{\"conditions\":[{\"type\":\"kill\",\"entity\":\"test:entity\",\"count\":2}]}]}").getAsJsonObject();
        BookEntry stagedRestored = BookEntry.fromJson(STAGED, stagedJson, Map.of(), RegistryAccess.EMPTY);
        check(stagedRestored != null && stagedRestored.stages().size() == 2, "stage json round-trip");
        check(stagedRestored != null && stagedRestored.stages().get(1).get(0) instanceof UnlockCondition.Kill, "stage condition parser");
        JsonObject hiddenJson = JsonParser.parseString("{\"chapter\":\"test:chapter\",\"title\":\"Hidden\",\"visibility\":\"hidden_until\",\"icon\":{\"atlas\":\"test:textures/test.png\",\"w\":16,\"h\":16,\"width\":16,\"height\":16},\"hidden_until\":[{\"type\":\"craft\",\"item\":\"test:item\"}]}").getAsJsonObject();
        BookEntry hiddenRestored = BookEntry.fromJson(HIDDEN, hiddenJson, Map.of(), RegistryAccess.EMPTY);
        check(hiddenRestored != null && hiddenRestored.visibility() == EntryVisibility.HIDDEN_UNTIL, "hidden visibility round-trip");
        check(hiddenRestored != null && hiddenRestored.hiddenUntil().size() == 1, "hidden conditions round-trip");

        BookProgress stageJsonProgress = new BookProgress();
        stageJsonProgress.setStage(STAGED, 1);
        BookProgress stageJsonRestored = BookProgress.fromJson(stageJsonProgress.toJson());
        check(stageJsonRestored.stageOf(STAGED) == 1, "stage progress json");

        System.out.println("Etericon progress self-test passed");
    }

    private static void check(boolean condition, String name) {
        if (!condition) { throw new AssertionError(name); }
    }
}
