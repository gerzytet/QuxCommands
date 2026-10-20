package me.github.gerzytet.QuxCommands;

import me.github.gerzytet.QuxCommands.bingo.BingoListener;
import me.github.gerzytet.QuxCommands.bingo.BingoManager;
import me.github.gerzytet.QuxCommands.bingo.BingoMap;
import me.github.gerzytet.QuxCommands.commandModules.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class QuxCommands extends JavaPlugin
{
	public static QuxCommands instance;
	@Override
	public void onEnable()
	{
        instance = this;
        TrivialCommands.init();
		Replace.init();
		WorldeditCommands.init();
		BingoMap.init();

		Bukkit.getScheduler().runTaskTimer(this, BingoManager::scan, 0, 5);
		QuxScoreboard.init();
		BingoManager.stop();
		getServer().getPluginManager().registerEvents(new EpicListener(), this);
		getServer().getPluginManager().registerEvents(new BingoListener(), this);
        try {
            Class.forName("github.scarsz.discordsrv.DiscordSRV");
            DiscordCommands.init();
        } catch (ClassNotFoundException e) {
            //don't load discord commands
        }
    }

	@Override
	public void onDisable() {
	}
}