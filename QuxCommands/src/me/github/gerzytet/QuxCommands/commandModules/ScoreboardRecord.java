package me.github.gerzytet.QuxCommands.commandModules;

import dev.jorel.commandapi.wrappers.SimpleFunctionWrapper;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
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
        Objective newObjective = scoreboard.getObjective(objective.getName());
        if (newObjective == null) {
            newObjective = scoreboard.registerNewObjective(objective.getName(), Criteria.DUMMY, objective.getDisplayName());
        }

        return newObjective;
    }

    public void clearSlot(DisplaySlot slot) {
        for (String key : slots.keySet()) {
            if (slots.get(key) == slot) {
                slots.remove(key);
                scoreboard.clearSlot(slot);
            }
        }
    }
}