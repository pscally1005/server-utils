package com.scally.serverutils.fillcontainer;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.distribution.DistributionParser;
import com.scally.serverutils.distribution.InvalidDistributionException;
import com.scally.serverutils.tabcompleter.TabCompleteCoordinate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class FillContainerCommandExecutor implements CommandExecutor, TabCompleter {

    // TODO: how to handle double chests?
    private static final Set<Material> ALLOWED_MATERIALS = Set.of(
            Material.CHEST,
            Material.BARREL,
            Material.SHULKER_BOX
    );

    /**
     * /fill-container x y z distribution
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (args.length != 4) {
            return false;
        }

        Entity entity;
        try {
            entity = (Entity) sender;
        } catch (ClassCastException exception) {
            ChatMessageUtils.sendError(sender, "Sender must be an entity!");
            return false;
        }

        // TODO: create common coords parsing function?
        final int[] coords = new int[3];
        for (int i = 0; i < 3; i++) {
            try {
                coords[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException exception) {
                ChatMessageUtils.sendError(sender, "Coordinates must be a valid number!");
                return false;
            }
        }

        // TODO: move exception handling up into ServerUtils
        Distribution distribution;
        try {
            distribution = DistributionParser.parse(args[3]);
        } catch (InvalidDistributionException exception) {
            ChatMessageUtils.sendError(sender, exception.getMessage());
            return false;
        }

        final World world = entity.getWorld();
        final Block block = world.getBlockAt(coords[0], coords[1], coords[2]);
        if (!ALLOWED_MATERIALS.contains(block.getType())) {
            ChatMessageUtils.sendError(sender, String.format("Invalid block at position. Found %s", block.getType()));
            return false;
        }

        final Container container = (Container) block.getState();
        final Inventory inventory = container.getInventory();
        distribution.fill(inventory);

        ChatMessageUtils.sendSuccess(sender, "Success!");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }

        if (args.length >= 3) {
            return List.of();
        }

        final RayTraceResult rayTraceResult = player.rayTraceBlocks(5);
        if (rayTraceResult == null) {
            return List.of();
        }

        final Block hitBlock = rayTraceResult.getHitBlock();
        if (hitBlock == null) {
            return List.of();
        }

        final TabCompleteCoordinate coordinate = TabCompleteCoordinate.forOneCoordinateCommand(args.length);
        return coordinate.getTabCompleteAbsoluteCoordinates(hitBlock.getLocation());
    }
}
