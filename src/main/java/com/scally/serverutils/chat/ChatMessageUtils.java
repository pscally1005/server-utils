package com.scally.serverutils.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class ChatMessageUtils {

    private static final ChatColor SUCCESS_COLOR = ChatColor.GREEN;
    private static final ChatColor ERROR_COLOR = ChatColor.RED;

    private ChatMessageUtils() {}

    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(SUCCESS_COLOR + message);
    }

    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(ERROR_COLOR + message);
    }
}
