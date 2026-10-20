//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.github.gerzytet.QuxCommands.bingo;

import java.util.*;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class BingoManager {
    private static HashMap<String, BingoMap> maps;
    private static boolean started;
    private static ArrayList<String> players;
    private static boolean singleCardMode;
    private static Queue<Location> bingoSpawns;
    private static final int NUM_SPAWNS = 8;
    private static Random random = new Random();
    private static final long radius = 10000000;
    private static ChunkyQueueManager chunkyQueue;

    private BingoManager() {
    }

    public static void addPlayer(String player) {
        players.add(player);
    }

    public static void removePlayer(String player) {
        players.remove(player);
    }

    public static int countPlayers() {
        return players.size();
    }

    public static ArrayList<String> getPlayers() {
        return players;
    }

    public static boolean getMode() {
        return singleCardMode;
    }

    public static void setMode(boolean mode) {
        singleCardMode = mode;
    }

    private static Location randomLoc() {
        World world = Bukkit.getWorld("bingo");
        double x = random.nextLong(radius*2L)-radius;
        double z = random.nextLong(radius*2L)-radius;
        double y = world.getHighestBlockYAt(new Location(world, x, 0, z));
        return new Location(world, x, y, z);
    }

    private static void addSpawn() {
        Location loc = randomLoc();
        bingoSpawns.add(loc);
        chunkyQueue.queueTask(loc.getBlockX(), loc.getBlockZ(), 8*16);
    }

    public static void spawnIn(Player player) {
        Location loc = bingoSpawns.poll();
        if (loc == null) {
            player.teleport(randomLoc());
            return;
        }
        player.teleport(loc);
    }

    public static void initBingoSpawns() {
        bingoSpawns = new ArrayDeque<>();
        chunkyQueue = new ChunkyQueueManager("bingo");
        for (int i = 0; i < NUM_SPAWNS; i++) {
            addSpawn();
        }
    }

    public static boolean isParticipating(String player) {
        for(String p : players) {
            if (p.equals(player)) {
                return true;
            }
        }

        return false;
    }

    public static void start() {
        setMode(true);
        var players = Bukkit.getOnlinePlayers();
        var main = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        Team team = main.getTeam("bingo");
        World world = Bukkit.getServer().getWorld("bingo");
        world.setTime(0);
        world.setWeatherDuration(0);
        ItemStack compass = new ItemStack(Material.CLOCK, 1);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Uses left: 5");
        var meta = compass.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName("World Compass");
        compass.setItemMeta(meta);
        for (Player p : players) {
            if (team.hasEntry(p.getName())) {
                addPlayer(p.getName());
                p.getInventory().clear();
                spawnIn(p);
                p.getInventory().addItem(compass);
            }
        }

        started = true;
        maps = new HashMap<String, BingoMap>();
        BingoMap map = new BingoMap();
        map.populate();

        for(String player : BingoManager.players) {
            if (!singleCardMode) {
                map = new BingoMap();
                map.populate();
            } else {
                map = map.clone();
            }

            maps.put(player, map);
        }

    }

    public static void stop() {
        started = false;
        players = new ArrayList<>();
        maps = new HashMap<>();
    }

    public static boolean isStarted() {
        return started;
    }

    public static BingoMap getMap(String player) {
        return (BingoMap)maps.get(player);
    }

    public static boolean handleItemPickup(String player, ItemStack item) {
        BingoMap map = (BingoMap)maps.get(player);
        if (map.hasItem(item) && !map.hasCollected(item)) {
            map.collect(item);
            return true;
        } else {
            return false;
        }
    }

    public static String checkWinner() {
        for(String player : maps.keySet()) {
            if (((BingoMap)maps.get(player)).hasWon()) {
                return player;
            }
        }

        return "";
    }

    public static void win(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return;
        }
        Score score = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("playerpoints").getScore(name);
        score.setScore(score.getScore()+1);
        for (String s : Bukkit.getScoreboardManager().getMainScoreboard().getTeam("bingo").getEntries()) {
            Player target = Bukkit.getPlayer(s);
            if (target == null) {
                continue;
            }

            target.spigot().sendMessage(
                new ComponentBuilder(name + " has gotten bingo!").color(ChatColor.GREEN).build()
            );
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp @a[team=bingo] @n[name=Hub]");
        BingoManager.stop();
    }

    public static void scan() {
        if (!started) {
            return;
        }
        for (String name : players) {
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                continue;
            }

            if (!Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName()).getName().equals("bingo")) {
                continue;
            }

            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null) {
                    continue;
                }
                boolean isNew = handleItemPickup(name, item);
                if (isNew) {
                    player.sendMessage("You collected an item!");
                    String winner = checkWinner();
                    if (!"".equals(winner)) {
                        win(winner);
                    }
                }
            }
        }
    }
}
