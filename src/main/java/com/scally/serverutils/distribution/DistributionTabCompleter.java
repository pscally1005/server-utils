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
        final TabCompleteParams params = calculateTagCompleteParams(arg);
        return filterMaterials(tag, params);
    }

    record TabCompleteParams(String filter, String prefix) {}

    private TabCompleteParams calculateTagCompleteParams(String arg) {
        final int lastPercentIndex = arg.lastIndexOf("%");
        final int lastCommaIndex = arg.lastIndexOf(",");

        if (lastPercentIndex == -1 && lastCommaIndex == -1) {
            return new TabCompleteParams(arg, "");
        } else if (lastPercentIndex > lastCommaIndex) {
            final String filter = arg.substring(lastPercentIndex + 1);
            final String prefix = arg.substring(0, lastPercentIndex + 1);
            return new TabCompleteParams(filter, prefix);
        } else {
            final String filter = arg.substring(lastCommaIndex + 1);
            final String prefix = arg.substring(0, lastCommaIndex + 1);
            return new TabCompleteParams(filter, prefix);
        }
    }

    private List<String> filterMaterials(Tag<Material> tag, TabCompleteParams params) {
        return tag.getValues()
                .stream()
                .map(Material::toString)
                .map(String::toLowerCase)
                .filter(s -> s.startsWith(params.filter))
                .sorted()
                .map(s -> params.prefix + s)
                .toList();
    }

    List<String> onTabCompleteDistribution(String arg);

}
