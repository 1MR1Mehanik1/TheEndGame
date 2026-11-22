package the_end_game.init.item.etericon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import the_end_game.init.item.etericon.api.EtericonApi;
import the_end_game.init.item.etericon.book.BookCatalog;
import the_end_game.init.item.etericon.book.BookEntry;
import the_end_game.init.item.etericon.book.BookManager;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.book.EtericonValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class EtericonCommands {
    private static final int OP_LEVEL = 2;

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("etericon")
                        .requires(source -> source.hasPermission(OP_LEVEL))
                        .then(Commands.literal("study")
                                .then(Commands.literal("get")
                                        .then(entryNode(EntryState.OPENED, true))
                                        .then(categoryNode(EntryState.OPENED, true))
                                        .then(allNode(EntryState.OPENED, true)))
                                .then(Commands.literal("forget")
                                        .then(entryNode(EntryState.LOCKED, false))
                                        .then(categoryNode(EntryState.LOCKED, false))
                                        .then(allNode(EntryState.LOCKED, false)))
                                .then(Commands.literal("points")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(context -> applyPoints(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "amount")))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> applyPoints(context, EntityArgument.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))))
                                .then(Commands.literal("theory")
                                        .then(Commands.argument("theory", ResourceLocationArgument.id())
                                                .executes(context -> applyTheory(context, context.getSource().getPlayerOrException(), ResourceLocationArgument.getId(context, "theory")))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> applyTheory(context, EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "theory"))))))
                                .then(Commands.literal("mark")
                                        .then(Commands.argument("target", ResourceLocationArgument.id())
                                                .executes(context -> applyStudy(context, context.getSource().getPlayerOrException(), ResourceLocationArgument.getId(context, "target")))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> applyStudy(context, EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "target")))))))
                        .then(Commands.literal("validate")
                                .executes(EtericonCommands::validate))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> entryNode(EntryState state, boolean grant) {
        return Commands.literal("entry")
                .then(Commands.argument("entry", ResourceLocationArgument.id())
                        .suggests(EtericonCommands::suggestEntries)
                        .executes(context -> applyEntry(context, context.getSource().getPlayerOrException(), state, grant))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> applyEntry(context, EntityArgument.getPlayer(context, "player"), state, grant))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> categoryNode(EntryState state, boolean grant) {
        return Commands.literal("category")
                .then(Commands.argument("category", ResourceLocationArgument.id())
                        .suggests(EtericonCommands::suggestCategories)
                        .executes(context -> applyCategory(context, context.getSource().getPlayerOrException(), state, grant))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> applyCategory(context, EntityArgument.getPlayer(context, "player"), state, grant))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> allNode(EntryState state, boolean grant) {
        return Commands.literal("all")
                .executes(context -> applyAll(context, context.getSource().getPlayerOrException(), state, grant))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> applyAll(context, EntityArgument.getPlayer(context, "player"), state, grant)));
    }

    private static int applyEntry(CommandContext<CommandSourceStack> context, ServerPlayer target, EntryState state, boolean grant) {
        ResourceLocation id = ResourceLocationArgument.getId(context, "entry");
        BookCatalog catalog = BookManager.getOrJavaOnly();
        if (catalog.entry(id) == null) {
            context.getSource().sendFailure(Component.translatable("commands.the_end_game.etericon.unknown_entry", id.toString()));
            return 0;
        }
        EtericonApi.setState(target, id, state);
        String key = grant ? "commands.the_end_game.etericon.get.entry" : "commands.the_end_game.etericon.forget.entry";
        context.getSource().sendSuccess(() -> Component.translatable(key, id.toString(), target.getDisplayName()), true);
        return 1;
    }

    private static int applyCategory(CommandContext<CommandSourceStack> context, ServerPlayer target, EntryState state, boolean grant) {
        ResourceLocation category = ResourceLocationArgument.getId(context, "category");
        BookCatalog catalog = BookManager.getOrJavaOnly();
        if (catalog.chapter(category) == null) {
            context.getSource().sendFailure(Component.translatable("commands.the_end_game.etericon.unknown_category", category.toString()));
            return 0;
        }
        List<BookEntry> entries = catalog.entriesFor(category);
        if (entries.isEmpty()) {
            context.getSource().sendFailure(Component.translatable("commands.the_end_game.etericon.empty_category", category.toString()));
            return 0;
        }
        List<ResourceLocation> ids = new ArrayList<>();
        for (BookEntry entry : entries) { ids.add(entry.id()); }
        EtericonApi.setStates(target, ids, state);
        String key = grant ? "commands.the_end_game.etericon.get.category" : "commands.the_end_game.etericon.forget.category";
        context.getSource().sendSuccess(() -> Component.translatable(key, category.toString(), target.getDisplayName()), true);
        return ids.size();
    }

    private static int applyAll(CommandContext<CommandSourceStack> context, ServerPlayer target, EntryState state, boolean grant) {
        BookCatalog catalog = BookManager.getOrJavaOnly();
        if (catalog.entries().isEmpty()) {
            context.getSource().sendFailure(Component.translatable("commands.the_end_game.etericon.empty"));
            return 0;
        }
        EtericonApi.setStates(target, catalog.entries().keySet(), state);
        String key = grant ? "commands.the_end_game.etericon.get.all" : "commands.the_end_game.etericon.forget.all";
        context.getSource().sendSuccess(() -> Component.translatable(key, target.getDisplayName()), true);
        return catalog.entries().size();
    }

    private static int applyPoints(CommandContext<CommandSourceStack> context, ServerPlayer target, int amount) {
        EtericonApi.addResearchPoints(target, amount);
        context.getSource().sendSuccess(() -> Component.translatable("commands.the_end_game.etericon.points", amount, target.getDisplayName()), true);
        return amount;
    }

    private static int applyTheory(CommandContext<CommandSourceStack> context, ServerPlayer target, ResourceLocation theory) {
        EtericonApi.completeTheory(target, theory);
        context.getSource().sendSuccess(() -> Component.translatable("commands.the_end_game.etericon.theory", theory.toString(), target.getDisplayName()), true);
        return 1;
    }

    private static int applyStudy(CommandContext<CommandSourceStack> context, ServerPlayer target, ResourceLocation study) {
        EtericonApi.markStudied(target, study);
        context.getSource().sendSuccess(() -> Component.translatable("commands.the_end_game.etericon.mark", study.toString(), target.getDisplayName()), true);
        return 1;
    }

    private static int validate(CommandContext<CommandSourceStack> context) {
        List<EtericonValidator.Issue> issues = EtericonValidator.validate(BookManager.getOrJavaOnly());
        long errors = EtericonValidator.errorCount(issues);
        long warnings = EtericonValidator.warningCount(issues);
        if (issues.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.the_end_game.etericon.validate.ok"), false);
            return 1;
        }
        context.getSource().sendSuccess(() -> Component.translatable("commands.the_end_game.etericon.validate.summary", errors, warnings), false);
        int shown = 0;
        for (EtericonValidator.Issue issue : issues) {
            if (shown >= 10) { break; }
            context.getSource().sendSuccess(() -> Component.literal(issue.toString()), false);
            shown++;
        }
        return errors == 0 ? 1 : 0;
    }

    private static CompletableFuture<Suggestions> suggestEntries(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (ResourceLocation id : BookManager.getOrJavaOnly().entries().keySet()) { builder.suggest(id.toString()); }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestCategories(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (ResourceLocation id : BookManager.getOrJavaOnly().chapters().keySet()) { builder.suggest(id.toString()); }
        return builder.buildFuture();
    }
}
