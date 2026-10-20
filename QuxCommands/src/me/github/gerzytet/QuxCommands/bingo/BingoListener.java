package me.github.gerzytet.QuxCommands.bingo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class BingoListener implements Listener {
    @EventHandler
    public void onServerLoad(ServerLoadEvent load) {
        BingoManager.initBingoSpawns();
    }
}

