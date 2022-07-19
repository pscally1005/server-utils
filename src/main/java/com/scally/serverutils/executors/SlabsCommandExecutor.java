package com.scally.serverutils.executors;

import com.scally.serverutils.chat.ChatMessageSender;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.distribution.DistributionPair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

// TODO: unit tests
// TODO: sel wand
// TODO: undo
// TODO: implement tilda tab-complete + tilda coords
//       getTargetBlock doesn't work when block isnt in range

public class SlabsCommandExecutor implements CommandExecutor, TabCompleter {

    public static final int VOLUME_LIMIT = 64 * 64 * 64;

    private final ChatMessageSender messageSender;

    public SlabsCommandExecutor() {
        this.messageSender = new ChatMessageSender();
    }

    public SlabsCommandExecutor(ChatMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        // /slabs <x1> <y1> <z1> <x2> <y2> <z2> <from-slab> <to-slab>
        if (args.length != 8) {
            messageSender.sendError(commandSender, "Invalid number of args!");
            return false;
        }

        if (!(commandSender instanceof Player)) {
            messageSender.sendError(commandSender, "Must be sent by a player!");
            return false;
        }

        final Player player = (Player) commandSender;
        final int[] coords = getCoordinates(player, args);

        // verify that the volume is under a certain size
        final int x1 = coords[0];
        final int y1 = coords[1];
        final int z1 = coords[2];

        final int x2 = coords[3];
        final int y2 = coords[4];
        final int z2 = coords[5];

        final long volume = Math.abs(x2 - x1) * Math.abs(y2 - y1) * Math.abs(z2 - z1);
        if (volume > VOLUME_LIMIT) {
            messageSender.sendError(commandSender, String.format("Volume must be less than %d blocks", VOLUME_LIMIT));
            return false;
        }

        Distribution fromDistribution = isValidSlabsDistribution(args[6]);
        Distribution toDistribution = isValidSlabsDistribution(args[7]);
        if(fromDistribution == null || toDistribution == null) {
            messageSender.sendError(commandSender, "Slab blocks must be valid!");
            return false;
        }

        final int min_x = Math.min(x1, x2);
        final int min_y = Math.min(y1, y2);
        final int min_z = Math.min(z1, z2);

        final int max_x = Math.max(x1, x2);
        final int max_y = Math.max(y1, y2);
        final int max_z = Math.max(z1, z2);

        World world = player.getWorld();
        int changedCount = 0;

        for(int x = min_x; x <= max_x; x++) {
            for(int y = min_y; y <= max_y; y++) {
                for(int z = min_z; z <= max_z; z++) {

                    Block block = world.getBlockAt(x, y, z);
                    BlockData bd = block.getBlockData();
                    Material mat = bd.getMaterial();

                    if(fromDistribution.hasMaterial(mat) == true) {

                        Slab slab = (Slab) bd;
                        Slab.Type type = slab.getType();
                        boolean isWaterlogged = slab.isWaterlogged();
                        Material toSlab = toDistribution.pick();
                        block.setType(toSlab, false);
                        bd = block.getBlockData();
                        ((Slab) bd).setWaterlogged(isWaterlogged);
                        ((Slab) bd).setType(type);
                        world.setBlockData(x, y, z, bd);
                        changedCount++;

                    }

                }
            }
        }

        messageSender.sendSuccess(commandSender, String.format("Success! %d blocks changed.", changedCount));
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.EMPTY_LIST;
        }
        Player player = (Player) sender;
        Block targ = player.getTargetBlock(Set.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER), 5);
        switch(args.length) {
            case 1, 4:
                return List.of(targ.getX() + "", targ.getX() + " " + targ.getY(), targ.getX() + " " + targ.getY() + " " + targ.getZ() );
            case 2, 5:
                return List.of(targ.getY() + "", targ.getY() + " " + targ.getZ() );
            case 3, 6:
                return List.of(targ.getZ() + "" );
            case 7, 8:
                return onTabCompleteDistribution(args[args.length-1]);
        }
        return Collections.EMPTY_LIST;
    }

    public int[] getCoordinates(Player player, String[] args) {

        int[] coords = new int[6];
        final Location loc = player.getLocation();
        for(int i = 0; i < coords.length; i++) {
            boolean isRelative = false;
            if(args[i].startsWith("~")) {

                if(args[i].equals('~')) {
                    if(i == 0 || i == 3) { coords[i] = (int) loc.getX(); }
                    else if(i == 1 || i == 4) { coords[i] = (int) loc.getY(); }
                    else if(i == 2 || i == 5) { coords[i] = (int) loc.getZ(); }
                    continue;
                }
                args[i] = args[i].substring(1);
                isRelative = true;
            }

            try {
                coords[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException exception) {
                messageSender.sendError(player, "Coordinates must be a valid number!");
                return null;
            }

            if(isRelative == true) {
                if(i == 0 || i == 3) { coords[i] = ((int) loc.getX()) + coords[i]; }
                else if(i == 1 || i == 4) { coords[i] = ((int) loc.getY()) + coords[i]; }
                else if(i == 2 || i == 5) { coords[i] = ((int) loc.getZ()) + coords[i]; }
            }

        }
        return coords;

    }

    List<String> onTabCompleteDistribution(String arg) {

        char lastChar = 0;
        if (!arg.equals("")) {
            lastChar = arg.charAt(arg.length() - 1);
        }

        if(lastChar == '%' || arg.equals("")) {
            return Tag.SLABS.getValues()
                    .stream()
                    .map(Material::toString)
                    .map(String::toLowerCase)
                    .sorted()
                    .map(s -> new StringBuilder(arg).append(s).toString())
                    .collect(Collectors.toList());

        } else {
            int lastPercent = arg.lastIndexOf("%");
            int lastComma = arg.lastIndexOf(",");
            String lastPart, firstPart;

            if(lastPercent == -1 && lastComma == -1) {
                lastPart = arg;
                firstPart = "";
            } else if(lastPercent >= lastComma) {
                lastPart = arg.substring(lastPercent+1);
                firstPart = arg.substring(0,lastPercent+1);
            } else {
                lastPart = arg.substring(lastComma+1);
                firstPart = arg.substring(0,lastComma+1);
            }

            return Tag.SLABS.getValues()
                    .stream()
                    .map(Material::toString)
                    .map(String::toLowerCase)
                    .filter(s -> s.startsWith(lastPart))
                    .sorted()
                    .map(s -> new StringBuilder(firstPart).append(s).toString())
                    .collect(Collectors.toList());

        }

    }

    private Slab getSlab(String arg) {
        final Material material = Material.matchMaterial(arg);
        if (material == null) {
            return null;
        }

        final BlockData blockData = material.createBlockData();
        if (blockData instanceof Slab) {
            return (Slab) blockData;
        }
        return null;
    }

    private Distribution isValidSlabsDistribution(String arg) {

        final Distribution dist = Distribution.parse(arg);
        if(dist == null) {
            return null;
        }

        final List<DistributionPair> fromPairs = dist.getPairs();
        for(DistributionPair pair : fromPairs) {
            final BlockData blockData = pair.getMaterial().createBlockData();
            if(!(blockData instanceof Slab)) {
                return null;
            }
        }

        return dist;

    }

}
