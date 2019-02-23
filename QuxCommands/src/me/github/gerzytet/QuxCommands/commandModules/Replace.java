package me.github.gerzytet.QuxCommands.commandModules;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;

import io.github.jorelali.commandapi.api.CommandAPI;
import io.github.jorelali.commandapi.api.arguments.Argument;
import io.github.jorelali.commandapi.api.arguments.GreedyStringArgument;
import io.github.jorelali.commandapi.api.arguments.StringArgument;
import io.github.jorelali.commandapi.api.arguments.TextArgument;
import me.github.gerzytet.QuxCommands.CommandReplacer;

public class Replace 
{
	public static void init()
	{
		LinkedHashMap<String, Argument> arguments = new LinkedHashMap<String, Argument>();
	    arguments.put("identifier",  new StringArgument());
	    arguments.put("replacement", new TextArgument());
	    arguments.put("command",     new GreedyStringArgument());
	    
	    CommandAPI.getInstance().register("replace", arguments, (sender, args) ->
	    {
	    	String identifier = (String) args[0];
	    	String replacement = (String) args[1];
	    	String command = (String) args[2];
	    	
	    	String newCommand = CommandReplacer.replace(command, identifier, replacement);
	    	
	    	Bukkit.dispatchCommand(sender, newCommand);
	    });
	}
}
