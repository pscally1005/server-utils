package com.scally.serverutils.stairs;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.distribution.DistributionPair;
import com.scally.serverutils.distribution.DistributionTabCompleter;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StairsCommandExecutor implements CommandExecutor, DistributionTabCompleter {

    private final UndoManager undoManager;

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .build();

    public StairsCommandExecutor(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    // /stairs <x1> <y1> <z1> <x2> <y2> <z2> <from-stair> <to-stair>
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        final ValidationResult validationResult = inputValidator.validate(commandSender, args);
        if (!validationResult.validated()) {
            return false;
        }

        final Player player = (Player) commandSender;
        final Coordinates coordinates = validationResult.coordinates();

        Distribution fromDistribution = isValidStairsDistribution(args[6]);
        Distribution toDistribution = isValidStairsDistribution(args[7]);
        if(fromDistribution == null || toDistribution == null) {
            ChatMessageUtils.sendError(commandSender, "Stair blocks must be valid!");
            return false;
        }

        World world = player.getWorld();

        final StairsChangeset changeset = new StairsChangeset();

        for (int x = coordinates.minX(); x <= coordinates.maxX(); x++) {
            for (int y = coordinates.minY(); y <= coordinates.maxY(); y++) {
                for (int z = coordinates.minZ(); z <= coordinates.maxZ(); z++) {

                    Block block = world.getBlockAt(x, y, z);
                    BlockData bd = block.getBlockData();
                    Material mat = bd.getMaterial();

                    if (fromDistribution.hasMaterial(mat)) {

                        Stairs stair = (Stairs) bd;
                        Bisected.Half half = stair.getHalf();
                        BlockFace facing = stair.getFacing();
                        Stairs.Shape shape = stair.getShape();
                        boolean isWaterlogged = stair.isWaterlogged();

                        Material toMaterial = toDistribution.pick();
                        block.setType(toMaterial, false);
                        bd = block.getBlockData();
                        ((Stairs) bd).setHalf(half);
                        ((Stairs) bd).setFacing(facing);
                        ((Stairs) bd).setShape(shape);
                        ((Stairs) bd).setWaterlogged(isWaterlogged);
                        world.setBlockData(x, y, z, bd);

                        final Location loc = block.getLocation();
                        StairsChange stairsChange = new StairsChange(loc, stair.getMaterial(), toMaterial, half, facing, shape, isWaterlogged);
                        changeset.add(stairsChange);

                    }
                }
            }
        }

        undoManager.store(player, changeset);
        ChatMessageUtils.sendSuccess(commandSender, String.format("Success! %d blocks changed.", changeset.count()));
        return true;

    }

    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.STAIRS);
    }

    //TODO: make this more generic
    private Distribution isValidStairsDistribution(String arg) {

        final Distribution dist = Distribution.parse(arg);
        if(dist == null) {
            return null;
        }

        final List<DistributionPair> fromPairs = dist.getPairs();
        for(DistributionPair pair : fromPairs) {
            final BlockData blockData = pair.getMaterial().createBlockData();
            if(!(blockData instanceof Stairs)) {
                return null;
            }
        }

        return dist;

    }
}
