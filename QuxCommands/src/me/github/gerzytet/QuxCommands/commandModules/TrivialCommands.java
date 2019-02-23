package me.github.gerzytet.QuxCommands.commandModules;

import java.util.LinkedHashMap;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.github.jorelali.commandapi.api.CommandAPI;
import io.github.jorelali.commandapi.api.arguments.Argument;
import io.github.jorelali.commandapi.api.arguments.IntegerArgument;

public class TrivialCommands implements commandModule
{
	public static void init()
	{
	//THYME
		LinkedHashMap<String, Argument> args = new LinkedHashMap<>();
		args.put("time", new IntegerArgument());
		
		CommandAPI.getInstance().register("thyme", args, (sender, arguments) -> {
		    if(sender instanceof Player) {
		     	Player player = (Player) sender;
		        player.getWorld().setTime((int) arguments[0]);
		    }
		});
			
			
			//X
			args = new LinkedHashMap<>();
			CommandAPI.getInstance().register("getx", args, (s, arguments) -> {
				CommandSender sender;
				if (s instanceof ProxiedCommandSender)
				{
					sender = ((ProxiedCommandSender) s).getCallee();
				}
				else
				{
					sender = s;
				}
				if (sender instanceof BlockCommandSender)
				{
					return (int) ((BlockCommandSender) sender).getBlock().getLocation().getX();
				}
				else if (sender instanceof Entity)
				{
					return (int) ((Entity) sender).getLocation().getX();
				}
				else
				{
					CommandAPI.fail("Players and cmd blocks only");
					return 0;
				}
			});
			
			args = new LinkedHashMap<>();
			CommandAPI.getInstance().register("gety", args, (s, arguments) -> {
				CommandSender sender;
				if (s instanceof ProxiedCommandSender)
				{
					sender = ((ProxiedCommandSender) s).getCallee();
				}
				else
				{
					sender = s;
				}
				if (sender instanceof BlockCommandSender)
				{
					return (int) ((BlockCommandSender) sender).getBlock().getLocation().getY();
				}
				else if (sender instanceof Entity)
				{
					return (int) ((Entity) sender).getLocation().getY();
				}
				else
				{
					CommandAPI.fail("Players and cmd blocks only");
					return 0;
				}
			});
			
			//Z
			args = new LinkedHashMap<>();
			CommandAPI.getInstance().register("getz", args, (s, arguments) -> {
				CommandSender sender;
				if (s instanceof ProxiedCommandSender)
				{
					sender = ((ProxiedCommandSender) s).getCallee();
				}
				else
				{
					sender = s;
				}
				if (sender instanceof BlockCommandSender)
				{
					return (int) ((BlockCommandSender) sender).getBlock().getLocation().getZ();
				}
				else if (sender instanceof Entity)
				{
					return (int) ((Entity) sender).getLocation().getZ();
				}
				else
				{
					CommandAPI.fail("Players and cmd blocks only");
					return 0;
				}
			});
	}
}
