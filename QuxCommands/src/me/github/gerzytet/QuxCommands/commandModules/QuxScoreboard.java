package me.github.gerzytet.QuxCommands.commandModules;

import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.wrappers.ScoreboardSlot;
import me.github.gerzytet.QuxCommands.QuxCommands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    private static File getScoreboardConfig() {
        File file = new File(QuxCommands.instance.getDataFolder(), "qux_scoreboard.yml");

        if (!QuxCommands.instance.getDataFolder().exists()) {
            QuxCommands.instance.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                QuxCommands.instance.getLogger().severe("couldn't get data.yml: " + e.getMessage());
            }
        }
        return file;
    }

    public static void saveScoreboards() {
        File file = getScoreboardConfig();
        var config = new YamlConfiguration();
        var scoreboardsSection = config.createSection("scoreboards");
        for (ScoreboardRecord sr : scoreboards.values()) {
            var section = scoreboardsSection.createSection(sr.name);
            Map<String, String> slots = new HashMap<>();
            for (String key : sr.slots.keySet()) {
                slots.put(key, sr.slots.get(key).name());
            }
            section.createSection("slots", slots);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            QuxCommands.instance.getLogger().severe("couldn't save data.yml: " + e.getMessage());
        }
    }

    public static void loadScoreboards() {
        File file = getScoreboardConfig();
        scoreboards.clear();

        var config = YamlConfiguration.loadConfiguration(file);
        var scoreboardSection = config.getConfigurationSection("scoreboards");
        if (scoreboardSection == null) {
            return;
        }

        for (String name : scoreboardSection.getKeys(false)) {
            ScoreboardRecord scoreboardRecord = new ScoreboardRecord(name);
            scoreboards.put(name, scoreboardRecord);
            var section = scoreboardSection.getConfigurationSection(name);
            var slots = section.getConfigurationSection("slots");
            if (slots == null) {
                continue;
            }

            for (String objectiveName : slots.getKeys(false)) {
                String slotName = slots.getString(objectiveName);
                DisplaySlot slot = DisplaySlot.valueOf(slotName);
                Objective mainObjective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objectiveName);
                if (mainObjective == null) {
                    continue;
                }
                scoreboardRecord.getOrCreateObjective(mainObjective).setDisplaySlot(slot);
                scoreboardRecord.slots.put(objectiveName, slot);
            }
        }
    }

    public static void init() {
        loadScoreboards();

        var create = new CommandAPICommand("create")
            .withArguments(
                new StringArgument("name")
            ).executes((sender, args) -> {
                String name = (String) args.get("name");
                if (scoreboards.containsKey(name)) {
                    throw CommandAPI.failWithString("Scoreboard already exists: " + name);
                }
                scoreboards.put(name, new ScoreboardRecord(name));
                saveScoreboards();
                sender.sendMessage("Created scoreboard " + name);
            });

        var setdisplay = new CommandAPICommand("setdisplay")
            .withArguments(
                nameArg(),
                new ScoreboardSlotArgument("slot")
            )
            .withOptionalArguments(
                new ObjectiveArgument("objective")
            )
            .executes((sender, args) -> {
                String name = (String) args.get("name");
                ScoreboardRecord scoreboardRecord = getScoreboard(name);
                DisplaySlot slot = ((ScoreboardSlot) args.get("slot")).getDisplaySlot();
                if (args.get("objective") == null) {
                    scoreboardRecord.clearSlot(slot);
                    saveScoreboards();
                    sender.sendMessage("Cleared display slot " + ((ScoreboardSlot) args.get("slot")).name() + " of " + name);
                    return;
                }
                Objective objective = scoreboardRecord.getOrCreateObjective((Objective) args.get("objective"));

                objective.setDisplaySlot(slot);
                scoreboardRecord.slots.put(objective.getName(), slot);

                saveScoreboards();
                sender.sendMessage("Set display slot " + ((ScoreboardSlot) args.get("slot")).name() + " of " + name + " to objective " + objective.getName());
            });


        var view = new CommandAPICommand("view")
            .withArguments(
                nameArg(),
                new EntitySelectorArgument.ManyPlayers("players")
            )
            .executes((sender, args) -> {
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

                sender.sendMessage("Modified view of " + players.size() + " players");
            });

        var reset = new CommandAPICommand("reset")
            .withArguments(
                new EntitySelectorArgument.ManyPlayers("players")
            )
            .executes((sender, args) -> {
                @SuppressWarnings("unchecked")
                Collection<Player> players = (Collection<Player>) args.get("players");
                if (players == null) {
                    return;
                }

                for (Player player : players) {
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }

                sender.sendMessage("Reset view of " + players.size() + " players");
            });

        var reflect = new CommandAPICommand("reflect")
            .withArguments(
                nameArg(),
                new ObjectiveArgument("objective")
            )
            .executes((sender, args) -> {
                String name = (String) args.get("name");
                ScoreboardRecord scoreboardRecord = getScoreboard(name);

                Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
                Objective mainObjective = (Objective) args.get("objective");
                Scoreboard target = scoreboardRecord.scoreboard;
                Objective targetObjective = scoreboardRecord.getOrCreateObjective(mainObjective);

                for (String entry : main.getEntries()) {
                    Score s = mainObjective.getScore(entry);
                    if (s.isScoreSet()) {
                        targetObjective.getScore(entry).setScore(s.getScore());
                    }
                }

                sender.sendMessage("Reflected objective " + targetObjective.getName() + " onto scoreboard " + name);
            });
//start: 8:30

        new CommandAPICommand("qux_scoreboard")
            .withPermission(CommandPermission.OP)
            .withSubcommand(create)
            .withSubcommand(setdisplay)
            .withSubcommand(view)
            .withSubcommand(reset)
            .withSubcommand(reflect)
            .register();
    }
}
