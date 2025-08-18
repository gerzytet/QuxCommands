package me.github.gerzytet.QuxCommands.commandModules;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

class ScoreboardRecord {
    public String name;
    public HashMap<String, DisplaySlot> slots;
    public Scoreboard scoreboard;

    public ScoreboardRecord(String name) {
        this.name = name;
        this.slots = new HashMap<>();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public Objective getOrCreateObjective(final Objective objective) {
        Objective newObjective = scoreboard.getObjective(name);
        if (newObjective == null) {
            //noinspection deprecation
            newObjective = scoreboard.registerNewObjective(name, "dummy", objective.getDisplayName());
        }
        return newObjective;
    }
}