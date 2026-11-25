package the_end_game.player;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import the_end_game.init.item.etericon.api.EtericonApi;

public final class PlayerVariablesCommands {
    private static final int OP_LEVEL = 2;

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("gc")
                        .requires(source -> source.hasPermission(OP_LEVEL))
                        .then(Commands.literal("playervariables")
                                .then(variableNode(Variable.RESEARCH))
                                .then(variableNode(Variable.MIND))
                                .then(variableNode(Variable.FRENZY)))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> variableNode(Variable variable) {
        return Commands.literal(variable.id)
                .executes(context -> show(context, context.getSource().getPlayerOrException(), variable))
                .then(operationNode("add", variable, Operation.ADD))
                .then(operationNode("rem", variable, Operation.REM))
                .then(operationNode("set", variable, Operation.SET));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> operationNode(String id, Variable variable, Operation operation) {
        return Commands.literal(id)
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(context -> apply(context, context.getSource().getPlayerOrException(), variable, operation, IntegerArgumentType.getInteger(context, "amount")))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> apply(context, EntityArgument.getPlayer(context, "player"), variable, operation, IntegerArgumentType.getInteger(context, "amount")))));
    }

    private static int show(CommandContext<CommandSourceStack> context, ServerPlayer target, Variable variable) {
        int value = variable.value(PlayerVariablesApi.get(target));
        context.getSource().sendSuccess(() -> Component.translatable("commands.the_end_game.playervariables.show", variable.id, value), false);
        return value;
    }

    private static int apply(CommandContext<CommandSourceStack> context, ServerPlayer target, Variable variable, Operation operation, int amount) {
        int before = variable.value(PlayerVariablesApi.get(target));
        int after = switch (operation) {
            case ADD -> before + amount;
            case REM -> before - amount;
            case SET -> amount;
        };
        PlayerVariablesApi.mutate(target, variables -> variable.set(variables, after));
        if (variable == Variable.RESEARCH) { EtericonApi.sync(target); }
        context.getSource().sendSuccess(() -> Component.translatable("commands.the_end_game.playervariables.change", variable.id, before, after, target.getDisplayName()), true);
        return after;
    }

    private enum Operation {
        ADD, REM, SET
    }

    private enum Variable {
        RESEARCH("research"), MIND("mind"), FRENZY("frenzy");

        private final String id;

        Variable(String id) { this.id = id; }

        private int value(PlayerVariables variables) {
            return switch (this) {
                case RESEARCH -> variables.research();
                case MIND -> variables.mind();
                case FRENZY -> variables.frenzy();
            };
        }

        private void set(PlayerVariables variables, int value) {
            switch (this) {
                case RESEARCH -> variables.setResearch(value);
                case MIND -> variables.setMind(value);
                case FRENZY -> variables.setFrenzy(value);
            }
        }
    }
}
