//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.github.gerzytet.QuxCommands.bingo;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

public class BingoManager {
    private static HashMap<String, BingoMap> maps;
    private static boolean started;
    private static ArrayList<String> players;
    private static boolean singleCardMode;

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

    public static boolean isParticipating(String player) {
        for(String p : players) {
            if (p.equals(player)) {
                return true;
            }
        }

        return false;
    }

    public static void start() {
        started = true;
        maps = new HashMap<String, BingoMap>();
        BingoMap map = new BingoMap();
        map.populate();

        for(String player : players) {
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
}
