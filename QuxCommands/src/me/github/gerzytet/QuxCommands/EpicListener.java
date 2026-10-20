package me.github.gerzytet.QuxCommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class EpicListener implements Listener {
    public static boolean alertsEnabled;
    private boolean isCommandBlock(Material mat) {
        return mat == Material.COMMAND_BLOCK || mat == Material.CHAIN_COMMAND_BLOCK || mat == Material.REPEATING_COMMAND_BLOCK;
    }

    public static List<String> chunkString(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        if (text == null) return chunks;

        int length = text.length();
        for (int i = 0; i < length; i += chunkSize) {
            chunks.add(text.substring(i, Math.min(length, i + chunkSize)));
        }
        return chunks;
    }


    @EventHandler
    public void onRightClickCommandBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        // Check if the clicked block is a command block
        Material type = block.getType();
        if (!isCommandBlock(type)) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        Material mat = item.getType();
        if (mat != Material.STICK) {
            return;
        }

        var meta = item.getItemMeta();
        if (meta == null) return;

        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }

        event.setCancelled(true);
        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> commands_full = new ArrayList<>();
        HashSet<Location> visited = new HashSet<>();
        int counter = 1;
        while (true) {
            CommandBlock data = (CommandBlock) block.getBlockData();
            org.bukkit.block.CommandBlock state = (org.bukkit.block.CommandBlock) block.getState();
            String cmd = state.getCommand();
            commands_full.add(cmd);
            if (cmd.length() > 80) {
                cmd = cmd.substring(0, 80) + "...";
            }
            ChatColor color = (counter % 2 == 1) ? ChatColor.WHITE : ChatColor.YELLOW;
            commands.add(color + "" + counter + ". " + cmd);
            counter++;
            visited.add(block.getLocation());

            Vector dir = data.getFacing().getDirection();
            Location newLocation = block.getLocation().add(dir);
            block = newLocation.getBlock();
            if (visited.contains(newLocation) || !isCommandBlock(block.getType())) {
                break;
            }
        }

        // Build a component per command, each copying itself on click
        TextComponent fullMessage = new TextComponent("");

        for (int i = 0; i < commands.size(); i++) {
            String command = commands.get(i);
            String full_command = commands_full.get(i);
            TextComponent line = new TextComponent(command);
            line.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, full_command));
            line.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text(ChatColor.GRAY + "Click to copy this command")));

            fullMessage.addExtra(line);
            fullMessage.addExtra("\n");
        }

        // "Copy All" button, joining every command with newlines
        String combined = String.join("\n", commands_full);
        TextComponent copyAll = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "[Copy All]");
        copyAll.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, combined));
        copyAll.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.GRAY + "Click to copy all commands")));

        fullMessage.addExtra(copyAll);

        // Send the whole thing as one message
        player.spigot().sendMessage(fullMessage);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isCommandBlock(event.getBlock().getType())) {
            return;
        }

        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
            return;
        }

        String cmd = ((org.bukkit.block.CommandBlock) event.getBlock().getState()).getCommand();
        event.getPlayer().sendMessage("Command broken: ");
        event.getPlayer().sendMessage(chunkString(cmd, 200).toArray(new String[0]));
        QuxCommands.instance.getLogger().warning("Command broken: " + cmd);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent chunkUnload) {
        if (!alertsEnabled) return;
        Chunk chunk = chunkUnload.getChunk();
        if (chunk.getWorld().getName().equals("bingo")) {
            return;
        }
        ArrayList<org.bukkit.block.CommandBlock> commandBlocks = new ArrayList<>();
        for (BlockState tileEntity : chunk.getTileEntities()) {
            if (tileEntity.getBlock().getType() == Material.REPEATING_COMMAND_BLOCK) {
                commandBlocks.add((org.bukkit.block.CommandBlock) tileEntity);
            }
        }

        if (commandBlocks.isEmpty()) {
            return;
        }

        Location loc = commandBlocks.get(0).getLocation();
        QuxCommands.instance.getLogger().warning(
            "Command block at " + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
                " and " + (commandBlocks.size()-1) + " others were unloaded."
        );

        BaseComponent commandsUnloaded = new ComponentBuilder().append("Command block at " + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
            " and " + (commandBlocks.size()-1) + " others were unloaded.")
            .color(net.md_5.bungee.api.ChatColor.YELLOW).build();

        BaseComponent forceloadClickable = new ComponentBuilder().append("Click here to forceload")
            .underlined(true)
            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                "/execute in " + loc.getWorld().getKey() + " run forceload add " + loc.getBlockX() + " " + loc.getBlockZ()
            )).event(
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                new ComponentBuilder()
                    .append("Click me").build()
            ))).build();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.spigot().sendMessage(commandsUnloaded);
                player.spigot().sendMessage(forceloadClickable);
            }
        }
    }

    EpicListener() {
        alertsEnabled = true;
    }

    public static void toggleAlerts() {
        alertsEnabled = !alertsEnabled;
    }
}
