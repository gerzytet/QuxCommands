package me.github.gerzytet.QuxCommands.commandModules;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.CommandArgument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.CommandResult;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;

import java.util.Random;

public class DiscordCommands {
    public static Random random;
    public static void init() {
        new CommandAPICommand("discordping")
            .withArguments(
                new StringArgument("id"),
                new GreedyStringArgument("message")
            )
            .withPermission(
                CommandPermission.OP
            )
            .executes((sender, args) -> {
                DiscordUtil.queueMessage(DiscordSRV.getPlugin().getMainTextChannel(), "<@" + (String)args.get("id") + ">" + (String)args.get("message"));
            }).register();

        new CommandAPICommand("discordmsg")
                .withArguments(
                    new GreedyStringArgument("message")
                ).withPermission(
                    CommandPermission.OP
                ).executes((sender, args) -> {
                    DiscordUtil.queueMessage(DiscordSRV.getPlugin().getMainTextChannel(), (String)args.get("message"));
                }).register();
    }
}
