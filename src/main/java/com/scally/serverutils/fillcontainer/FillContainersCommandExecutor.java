package com.scally.serverutils.fillcontainer;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.distribution.DistributionParser;
import com.scally.serverutils.distribution.InvalidDistributionException;
import com.scally.serverutils.tabcompleter.TabCompleteCoordinate;
import com.scally.serverutils.validation.Coordinates;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FillContainersCommandExecutor implements CommandExecutor, TabCompleter {

    /**
     * /fill-containers <block> <x1> <y1> <z1> <x2> <y2> <z2> <distribution>
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (args.length != 8) {
            return false;
        }

        Entity entity;
        try {
            entity = (Entity) sender;
        } catch (ClassCastException exception) {
            ChatMessageUtils.sendError(sender, "Sender must be an entity!");
            return false;
        }

        // Parse block type (first argument)
        Material blockType;
        try {
            blockType = Material.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException exception) {
            ChatMessageUtils.sendError(sender, String.format("Invalid block type: %s", args[0]));
            return false;
        }

        // Validate that the block type is an allowed container material
        if (!ContainerUtils.isAllowedMaterial(blockType)) {
            ChatMessageUtils.sendError(sender, String.format("Block type %s is not an allowed container material.", blockType));
            return false;
        }

        // Parse coordinates (args 1-6)
        final int[] coords = new int[6];
        for (int i = 0; i < 6; i++) {
            try {
                coords[i] = Integer.parseInt(args[i + 1]);
            } catch (NumberFormatException exception) {
                ChatMessageUtils.sendError(sender, "Coordinates must be valid numbers!");
                return false;
            }
        }

        final Coordinates coordinates = new Coordinates(coords);

        // Parse distribution (last argument)
        Distribution distribution;
        try {
            distribution = DistributionParser.parse(args[7]);
        } catch (InvalidDistributionException exception) {
            ChatMessageUtils.sendError(sender, exception.getMessage());
            return false;
        }

        // Fill all containers in the volume
        final World world = entity.getWorld();
        int filledCount = 0;
        int skippedCount = 0;

        for (int x = coordinates.minX(); x <= coordinates.maxX(); x++) {
            for (int y = coordinates.minY(); y <= coordinates.maxY(); y++) {
                for (int z = coordinates.minZ(); z <= coordinates.maxZ(); z++) {
                    final Block block = world.getBlockAt(x, y, z);
                    
                    // Only fill blocks that match the specified type and are allowed container materials
                    if (block.getType() == blockType && ContainerUtils.isAllowedMaterial(block.getType())) {
                        if (ContainerUtils.fillContainer(block, distribution)) {
                            filledCount++;
                        } else {
                            skippedCount++;
                        }
                    } else {
                        skippedCount++;
                    }
                }
            }
        }

        ChatMessageUtils.sendSuccess(sender, String.format("Success! Filled %d container(s), skipped %d block(s).", filledCount, skippedCount));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }

        if (args.length >= 8) {
            return List.of();
        }

        // First argument: suggest allowed container materials
        if (args.length == 1) {
            return ContainerUtils.getAllowedMaterials().stream()
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .toList();
        }

        // Coordinate arguments: use tab completion for coordinates
        final RayTraceResult rayTraceResult = player.rayTraceBlocks(5);
        if (rayTraceResult == null) {
            return List.of();
        }

        final Block hitBlock = rayTraceResult.getHitBlock();
        if (hitBlock == null) {
            return List.of();
        }

        final TabCompleteCoordinate coordinate = TabCompleteCoordinate.forTwoCoordinateCommand(args.length - 1);
        return coordinate.getTabCompleteAbsoluteCoordinates(hitBlock.getLocation());
    }
}
