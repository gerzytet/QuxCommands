package me.github.gerzytet.QuxCommands.bingo;

import me.github.gerzytet.QuxCommands.QuxCommands;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.popcraft.chunky.api.ChunkyAPI;

import java.util.LinkedList;
import java.util.Queue;

public class ChunkyQueueManager {

    private ChunkyAPI chunky;
    private final Queue<double[]> queue = new LinkedList<>();
    private String world;

    public void queueTask(double centerX, double centerZ, double radius) {
        queue.add(new double[] {centerX, centerZ, radius});
        tryStartNext();
    }

    private void tryStartNext() {
        if (chunky.isRunning(world)) return; // something already running, wait for it to finish

        double[] params = queue.poll();
        if (params == null) return;

        boolean rtn = chunky.startTask(world, "square", params[0], params[1], params[2], params[2], "concentric");
        if (rtn) {
            QuxCommands.instance.getLogger().info("completed task");
        } else {
            QuxCommands.instance.getLogger().warning("failed chunky task");
        }
    }

    public ChunkyQueueManager(String world) {
        this.world = world;
        this.chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
        assert chunky != null;
        chunky.onGenerationComplete(event -> {
            if (event.world().equals(world)) {
                tryStartNext();
            }
        });
    }
}