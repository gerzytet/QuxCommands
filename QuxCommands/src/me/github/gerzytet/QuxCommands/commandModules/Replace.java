package me.github.gerzytet.QuxCommands.commandModules;

import java.util.LinkedHashMap;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import org.bukkit.Bukkit;

import me.github.gerzytet.QuxCommands.CommandReplacer;

public class Replace 
{
	public static void init()
	{
		new CommandAPICommand("replace")
				.withArguments(
						new StringArgument("identifier"),
						new TextArgument("replacement"),
						new GreedyStringArgument("command"))
				.executes((sender, args) -> {
					String identifier = (String) args.get(0);
					String replacement = (String) args.get(1);
					String command = (String) args.get(2);

					String newCommand = CommandReplacer.replace(command, identifier, replacement);

					Bukkit.dispatchCommand(sender, newCommand);
				}).register();
	}
}
