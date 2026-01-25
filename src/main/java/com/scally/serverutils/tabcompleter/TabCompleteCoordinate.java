package com.scally.serverutils.tabcompleter;

import org.bukkit.Location;

import java.util.List;

public enum TabCompleteCoordinate {
    X,
    Y,
    Z,
    UNKNOWN;

    TabCompleteCoordinate() {}

    public static TabCompleteCoordinate forOneCoordinateCommand(int argsLength) {
        return switch (argsLength) {
            case 1 -> TabCompleteCoordinate.X;
            case 2 -> TabCompleteCoordinate.Y;
            case 3 -> TabCompleteCoordinate.Z;
            default -> TabCompleteCoordinate.UNKNOWN;
        };
    }

    public static TabCompleteCoordinate forTwoCoordinateCommand(int argsLength) {
        return switch (argsLength) {
            case 1, 4 -> TabCompleteCoordinate.X;
            case 2, 5 -> TabCompleteCoordinate.Y;
            case 3, 6 -> TabCompleteCoordinate.Z;
            default -> TabCompleteCoordinate.UNKNOWN;
        };
    }

    public List<String> getTabCompleteAbsoluteCoordinates(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return switch (this) {
            case X -> List.of(x + "", x + " " + y, x + " " + y + " " + z);
            case Y -> List.of(y + "", y + " " + z);
            case Z -> List.of(z + "");
            case UNKNOWN -> List.of();
        };
    }

    public List<String> getTabCompleteRelativeCoordinates() {
        return switch (this) {
            case X -> List.of("~", "~ ~", "~ ~ ~");
            case Y -> List.of("~", "~ ~");
            case Z -> List.of("~");
            case UNKNOWN -> List.of();
        };
    }
}
