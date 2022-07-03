package com.scally.serverutils.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatMessageSender {

    private static final ChatColor SUCCESS_COLOR = ChatColor.GREEN;
    private static final ChatColor ERROR_COLOR = ChatColor.RED;

    public void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(SUCCESS_COLOR + message);
    }

    public void sendError(CommandSender sender, String message) {
        sender.sendMessage(ERROR_COLOR + message);
    }
}
