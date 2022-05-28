package com.scally.serverutils.executors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// TODO: unit tests
public class SlabsCommandExecutor implements CommandExecutor {

    private static final int VOLUME_LIMIT = 50 * 50 * 50;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        // /slabs <x1> <y1> <z1> <x2> <y2> <z2> <from-slab> <to-slab>
        if (args.length != 8) {
            return false;
        }

        // TODO: figure out how to tab-fill the coordinates

        final int[] coords = new int[6];
        for (int i = 0; i < 6; i++) {
            try {
                coords[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException exception) {
                commandSender.sendMessage("Coordinates must be a valid number!");
                return false;
            }
        }

        // verify that the volume is under a certain size
        final int x1 = coords[0];
        final int y1 = coords[1];
        final int z1 = coords[2];

        final int x2 = coords[3];
        final int y2 = coords[4];
        final int z2 = coords[5];

        final long volume = Math.abs(x2 - x1) * Math.abs(y2 - y1) * Math.abs(z2 - z1);
        if (volume > VOLUME_LIMIT) {
            commandSender.sendMessage(String.format("Volume must be less than %d blocks", VOLUME_LIMIT));
            return false;
        }

        final Material fromSlab = getSlab(args[6]);
        final Material toSlab = getSlab(args[7]);

        if (fromSlab == null || toSlab == null) {
            commandSender.sendMessage("Slab blocks must be valid!");
            return false;
        }

        // TODO: actually do the replacement

        commandSender.sendMessage("That was valid but it's not implemented yet");
        return true;
    }

    // TODO: change to use Slab interface
    private Material getSlab(String arg) {
        if (!arg.toLowerCase().endsWith("_slab")) {
            return null;
        }
        return Material.matchMaterial(arg);
    }
}
