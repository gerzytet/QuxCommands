package me.github.gerzytet.QuxCommands;

import me.github.gerzytet.QuxCommands.bingo.BingoManager;
import me.github.gerzytet.QuxCommands.commandModules.QuxScoreboard;
import me.github.gerzytet.QuxCommands.commandModules.WorldeditCommands;
import org.bukkit.plugin.java.JavaPlugin;

import me.github.gerzytet.QuxCommands.commandModules.Replace;
import me.github.gerzytet.QuxCommands.commandModules.TrivialCommands;

public class QuxCommands extends JavaPlugin
{
	public static QuxCommands instance;
	@Override
	public void onEnable()
	{
		TrivialCommands.init();
		Replace.init();
		WorldeditCommands.init();
		QuxScoreboard.init();
		BingoManager.stop();
		getServer().getPluginManager().registerEvents(new EpicListener(), this);
		instance = this;
	}

	@Override
	public void onDisable() {
	}
}