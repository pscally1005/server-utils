package com.scally.serverutils.distribution;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface DistributionTabCompleter extends TabCompleter {

    default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            return Collections.EMPTY_LIST;
        }

        Player player = (Player) sender;
        final RayTraceResult rayTraceResult = player.rayTraceBlocks(5);
        if (rayTraceResult == null) {
            switch(args.length) {
                case 1,4:
                    return List.of("~", "~ ~", "~ ~ ~");
                case 2,5:
                    return List.of("~", "~ ~");
                case 3, 6:
                    return List.of("~");
                case 7, 8:
                    return onTabCompleteDistribution(args[args.length-1]);
                default:
                    return List.of();
            }
        }

        final Block targ = rayTraceResult.getHitBlock();
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

    default List<String> onTabCompleteDistribution(String arg, Tag<Material> tag) {

        char lastChar = 0;
        if (!arg.equals("")) {
            lastChar = arg.charAt(arg.length() - 1);
        }

        if(lastChar == '%' || arg.equals("")) {
            return tag.getValues()
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

            return tag.getValues()
                    .stream()
                    .map(Material::toString)
                    .map(String::toLowerCase)
                    .filter(s -> s.startsWith(lastPart))
                    .sorted()
                    .map(s -> new StringBuilder(firstPart).append(s).toString())
                    .collect(Collectors.toList());

        }

    }

    List<String> onTabCompleteDistribution(String arg);

}
