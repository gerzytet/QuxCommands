package me.github.gerzytet.QuxCommands;

import me.github.gerzytet.QuxCommands.bingo.BingoManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.github.gerzytet.QuxCommands.commandModules.Replace;
import me.github.gerzytet.QuxCommands.commandModules.TrivialCommands;

public class QuxCommands extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		TrivialCommands.init();
		Replace.init();
		BingoManager.stop();
	}

	@Override
	public void onDisable()
	{
	}
}