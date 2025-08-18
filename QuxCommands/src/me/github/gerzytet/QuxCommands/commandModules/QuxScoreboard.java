package me.github.gerzytet.QuxCommands.commandModules;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.wrappers.ScoreboardSlot;
import me.github.gerzytet.QuxCommands.QuxCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.HashMap;

public class QuxScoreboard {
    private static HashMap<String, ScoreboardRecord> scoreboards = new HashMap<>();
    private static String[] suggestScoreboardName(SuggestionInfo<CommandSender> info) {
        var arr = scoreboards.keySet().toArray(new String[0]);
        for (String name : arr) {
            QuxCommands.instance.getLogger().info(name);
        }
        return arr;
    }
    private static ScoreboardRecord getScoreboard(String name) throws WrapperCommandSyntaxException {
        if  (scoreboards.containsKey(name)) {
            return scoreboards.get(name);
        } else {
            throw CommandAPI.failWithString("Scoreboard does not exist: " + name);
        }
    }

    public static StringArgument nameArg() {
        ArgumentSuggestions<CommandSender> suggestions = ArgumentSuggestions.strings(QuxScoreboard::suggestScoreboardName);
        StringArgument na = new StringArgument("name");
        na.replaceSuggestions(suggestions);
        return na;
    }

    public static void init() {
        scoreboards.put("merpland", new ScoreboardRecord("merpland") {});

        new CommandTree("qux_scoreboard")
            .withPermission(CommandPermission.OP)
            .thenNested(
                new LiteralArgument("create", "create"),
                nameArg().executes((sender, args) -> {
                    String name = (String) args.get("name");
                    scoreboards.put(name, new ScoreboardRecord(name));
                })
            )

            .thenNested(
                new LiteralArgument("setdisplay", "setdisplay"),
                nameArg(),
                new ScoreboardSlotArgument("slot"),
                new ObjectiveArgument("objective").executes((sender, args) -> {
                    String name = (String) args.get("name");
                    ScoreboardRecord scoreboardRecord = getScoreboard(name);
                    DisplaySlot slot = ((ScoreboardSlot) args.get("slot")).getDisplaySlot();
                    Objective objective = scoreboardRecord.getOrCreateObjective((Objective) args.get("objective"));

                    objective.setDisplaySlot(slot);
                })
            )

            .thenNested(
                new LiteralArgument("view", "view"),
                nameArg(),
                new EntitySelectorArgument.ManyPlayers("players").executes((sender, args) -> {
                    String name = (String) args.get("name");
                    ScoreboardRecord scoreboardRecord = getScoreboard(name);

                    @SuppressWarnings("unchecked")
                    Collection<Player> players = (Collection<Player>) args.get("players");
                    if (players == null) {
                        return;
                    }

                    for (Player player : players) {
                        player.setScoreboard(scoreboardRecord.scoreboard);
                    }
                })
            ).register();
    }
}
