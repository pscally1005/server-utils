package com.scally.serverutils.distribution;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface DistributionTabCompleter extends TabCompleter {

    default List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                                       String[] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }

        if (args.length == 7 || args.length == 8) {
            return onTabCompleteDistribution(args[args.length-1]);
        }

        final RayTraceResult rayTraceResult = player.rayTraceBlocks(5);
        if (rayTraceResult == null) {
            return onTabCompleteRelativeCoordinates(args.length);
        }

        final Block hitBlock = rayTraceResult.getHitBlock();
        if (hitBlock != null) {
            return onTabCompleteAbsoluteCoordinates(args.length, hitBlock.getLocation());
        }

        return List.of();
    }

    private List<String> onTabCompleteRelativeCoordinates(int argsLength) {
        return switch (argsLength) {
            case 1, 4 -> List.of("~", "~ ~", "~ ~ ~");
            case 2, 5 -> List.of("~", "~ ~");
            case 3, 6 -> List.of("~");
            default -> List.of();
        };
    }

    private List<String> onTabCompleteAbsoluteCoordinates(int argsLength, Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return switch (argsLength) {
            case 1, 4 -> List.of(x + "", x + " " + y, x + " " + y + " " + z);
            case 2, 5 -> List.of(y + "", y + " " + z);
            case 3, 6 -> List.of(z + "");
            default -> List.of();
        };
    }

    default List<String> onTabCompleteDistribution(String arg, Tag<Material> tag) {
        final boolean suggestPercentages = shouldSuggestPercentages(arg);
        if (suggestPercentages) {
            return onTabCompleteMaterialPercentages(arg, tag);
        }
        return onTabCompleteMaterials(arg, tag);
    }

    private boolean shouldSuggestPercentages(String arg) {
        if ("".equals(arg)) {
            return false;
        }
        return arg.charAt(arg.length() - 1) != '%';
    }

    private List<String> onTabCompleteMaterialPercentages(String arg, Tag<Material> tag) {
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
                .map(s -> firstPart + s)
                .collect(Collectors.toList());
    }

    private List<String> onTabCompleteMaterials(String arg, Tag<Material> tag) {
        return tag.getValues()
                .stream()
                .map(Material::toString)
                .map(String::toLowerCase)
                .sorted()
                .map(s -> arg + s)
                .collect(Collectors.toList());
    }

    List<String> onTabCompleteDistribution(String arg);

}
