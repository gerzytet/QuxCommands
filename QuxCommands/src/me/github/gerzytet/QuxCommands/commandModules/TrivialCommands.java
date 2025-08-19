package me.github.gerzytet.QuxCommands.commandModules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import me.github.gerzytet.QuxCommands.EpicListener;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TrivialCommands implements commandModule
{
	public static void init()
	{
		new CommandAPICommand("thyme")
				.withArguments(new IntegerArgument("time"))
				.executes((sender, args) -> {
					if(sender instanceof Player player) {
                        player.getWorld().setTime((int) args.get("time"));
					}
				}).register();

		new CommandAPICommand("replacedatetime")
				.withArguments(new GreedyStringArgument("command"))
				.executes((sender, args) -> {
					LocalDateTime now = LocalDateTime.now();

					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

					String dateStr = now.format(dateFormatter);
					String timeStr = now.format(timeFormatter);

					String cmd = (String)args.get("command");
					cmd = cmd.replace("$date", dateStr).replace("$time", timeStr);

					Bukkit.dispatchCommand(sender, cmd);
				}).register();

        new CommandAPICommand("toggle_unload_alerts")
            .withArguments()
            .withPermission(CommandPermission.OP)
            .executes((sender, args) -> {
                EpicListener.toggleAlerts();
                sender.sendMessage("Show alerts: " + EpicListener.alertsEnabled);
            }).register();
			
			//X
			/*args = new LinkedHashMap<>();
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
			});*/
	}
}
