package me.github.gerzytet.QuxCommands.commandModules;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class WorldeditCommands implements commandModule {
    public static BlockVector3 toBlockVector(Location loc) {
        return new BlockVector3(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    public static void init() {
        new CommandAPICommand("we_clone")
                .withPermission(CommandPermission.OP)
                .withArguments(
                        new LocationArgument("from_1", LocationType.BLOCK_POSITION),
                        new LocationArgument("from_2", LocationType.BLOCK_POSITION),
                        new LocationArgument("to", LocationType.BLOCK_POSITION))
                .executes((sender, args) -> {
                    Location from1 = (Location) args.get(0);
                    Location from2 = (Location) args.get(1);
                    Location to1 = (Location) args.get(2);
                    assert from1 != null;
                    assert from2 != null;
                    assert to1 != null;
                    World weWorld = BukkitAdapter.adapt(from2.getWorld());
                    try (EditSession session = WorldEdit.getInstance().newEditSession(weWorld)) {
                        CuboidRegion region = new CuboidRegion(toBlockVector(from1), toBlockVector(from2));
                        var op = new ForwardExtentCopy(
                                session, region, session, toBlockVector(to1)
                        );
                        Operations.complete(op);
                    } catch (WorldEditException e) {
                        throw new RuntimeException(e);
                    }

                }).register();

        new CommandAPICommand("we_replace")
            .withPermission(CommandPermission.OP)
            .withArguments(
                new LocationArgument("pos1"),
                new LocationArgument("pos2"),
                new ItemStackArgument("from_block"),
                new ItemStackArgument("to_block")
            )
            .executes((sender, args) -> {
                Location pos1 = (Location) args.get("pos1");
                Location pos2 = (Location) args.get("pos2");
                ItemStack from_block = (ItemStack) args.get("from_block");
                ItemStack to_block = (ItemStack) args.get("to_block");

                assert from_block != null;
                assert to_block != null;
                Material from_mat = from_block.getType();
                Material to_mat = to_block.getType();
                if (!from_mat.isBlock() || !to_mat.isBlock()) {
                    throw CommandAPI.failWithString("not a block");
                }
                assert pos1 != null;
                World weWorld = BukkitAdapter.adapt(Objects.requireNonNull(pos1.getWorld()));
                try (EditSession session = WorldEdit.getInstance().newEditSession(weWorld)) {
                    assert pos2 != null;
                    CuboidRegion region = new CuboidRegion(toBlockVector(pos1), toBlockVector(pos2));
                    try {
                        var state = BukkitAdapter.asBlockType(to_mat).getDefaultState();
                        int changed = session.replaceBlocks(region, new BlockTypeMask(weWorld, BukkitAdapter.asBlockType(from_mat)), state);
                        sender.sendMessage("changed: " + changed);
                        return changed;
                    } catch (MaxChangedBlocksException e) {
                        throw new RuntimeException(e);
                    }
                }
                }).register();

        new CommandAPICommand("we_fill")
            .withPermission(CommandPermission.OP)
            .withArguments(
                new LocationArgument("pos1"),
                new LocationArgument("pos2"),
                new ItemStackArgument("block")
            )
            .executes((sender, args) -> {
                Location pos1 = (Location) args.get("pos1");
                Location pos2 = (Location) args.get("pos2");
                ItemStack block = (ItemStack) args.get("block");

                assert block != null;
                Material mat = block.getType();
                if (!mat.isBlock()) {
                    throw CommandAPI.failWithString("not a block");
                }
                assert pos1 != null;
                World weWorld = BukkitAdapter.adapt(Objects.requireNonNull(pos1.getWorld()));
                try (EditSession session = WorldEdit.getInstance().newEditSession(weWorld)) {
                    assert pos2 != null;
                    CuboidRegion region = new CuboidRegion(toBlockVector(pos1), toBlockVector(pos2));
                    try {
                        var state = BukkitAdapter.asBlockType(mat).getDefaultState();
                        int changed = session.setBlocks(region, state);
                        sender.sendMessage("changed: " + changed);
                        return changed;
                    } catch (MaxChangedBlocksException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).register();
    };
}
