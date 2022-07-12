package com.scally.serverutils.executors;

import com.scally.serverutils.chat.ChatMessageSender;
import com.scally.serverutils.distribution.Distribution;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FillContainerCommandExecutor implements CommandExecutor {

    // TODO: how to handle double chests?
    private static final Set<Material> ALLOWED_MATERIALS = Set.of(
            Material.CHEST,
            Material.BARREL,
            Material.SHULKER_BOX
    );

    private final ChatMessageSender messageSender = new ChatMessageSender();

    /**
     * /fill-container x y z distribution
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length != 4) {
            return false;
        }

        Entity entity;
        try {
            entity = (Entity) sender;
        } catch (ClassCastException exception) {
            messageSender.sendError(sender, "Sender must be an entity!");
            return false;
        }

        // TODO: create common coords parsing function?
        final int[] coords = new int[3];
        for (int i = 0; i < 3; i++) {
            try {
                coords[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException exception) {
                messageSender.sendError(sender, "Coordinates must be a valid number!");
                return false;
            }
        }

        final Distribution distribution = Distribution.parse(args[3]);
        if (distribution == null) {
            return false;
        }

        final World world = entity.getWorld();
        final Block block = world.getBlockAt(coords[0], coords[1], coords[2]);
        if (!ALLOWED_MATERIALS.contains(block.getType())) {
            messageSender.sendError(sender, String.format("Invalid block at position. Found %s", block.getType()));
            return false;
        }

        final Container container = (Container) block.getState();
        final Inventory inventory = container.getInventory();
        distribution.fill(inventory);

        messageSender.sendSuccess(sender, "Success!");
        return true;
    }


}
