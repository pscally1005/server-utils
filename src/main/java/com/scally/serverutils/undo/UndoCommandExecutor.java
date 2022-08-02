package com.scally.serverutils.undo;

import com.scally.serverutils.chat.ChatMessageSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UndoCommandExecutor implements CommandExecutor {

    private final ChatMessageSender messageSender;
    private final UndoManager undoManager;

    public UndoCommandExecutor(ChatMessageSender messageSender, UndoManager undoManager) {
        this.messageSender = messageSender;
        this.undoManager = undoManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        // TODO: look into making this an annotation
        if (!(commandSender instanceof Player)) {
            messageSender.sendError(commandSender, "Must be sent by a player!");
            return false;
        }

        final Player player = (Player) commandSender;
        return undoManager.undo(player);
    }
}
