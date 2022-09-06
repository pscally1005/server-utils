package com.scally.serverutils.validation;

import com.scally.serverutils.chat.ChatMessageUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InputValidator {

    public static boolean checkArgNumber(CommandSender commandSender, int argsNum, int correctNum) {

        if (argsNum != correctNum) {
            ChatMessageUtils.sendError(commandSender, "Invalid number of args!");
            return false;
        }

        return true;

    }

    public static boolean isPlayer(CommandSender commandSender) {

        if (!(commandSender instanceof Player)) {
            ChatMessageUtils.sendError(commandSender, "Must be sent by a player!");
            return false;
        }

        return true;

    }

    public static int[] parseArgs(Player player, String[] args) {

        int[] coords = new int[6];
        final Location loc = player.getLocation();
        for(int i = 0; i < coords.length; i++) {
            boolean isRelative = false;
            if(args[i].startsWith("~")) {

                if(args[i].equals("~")) {
                    if(i == 0 || i == 3) { coords[i] = loc.getBlockX(); }
                    else if(i == 1 || i == 4) { coords[i] = loc.getBlockY(); }
                    else if(i == 2 || i == 5) { coords[i] = loc.getBlockZ(); }
                    continue;
                }
                args[i] = args[i].substring(1);
                isRelative = true;
            }

            try {
                coords[i] = Integer.parseInt(args[i]);
            } catch (NumberFormatException exception) {
                return null;
            }

            if(isRelative == true) {
                if(i == 0 || i == 3) { coords[i] = loc.getBlockX() + coords[i]; }
                else if(i == 1 || i == 4) { coords[i] = loc.getBlockY() + coords[i]; }
                else if(i == 2 || i == 5) { coords[i] = loc.getBlockZ() + coords[i]; }
            }

        }
        return coords;

    }

}
