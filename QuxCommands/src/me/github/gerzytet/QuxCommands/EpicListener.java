package me.github.gerzytet.QuxCommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class EpicListener implements Listener {
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
        HashSet<Location> visited = new HashSet<>();
        int counter = 1;
        while (true) {
            CommandBlock data = (CommandBlock) block.getBlockData();
            org.bukkit.block.CommandBlock state = (org.bukkit.block.CommandBlock) block.getState();
            String cmd = state.getCommand();
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

        for (String command : commands) {
            player.sendMessage(command);
        }
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
}
