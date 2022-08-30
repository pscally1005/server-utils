package com.scally.serverutils.stairs;

import com.scally.serverutils.chat.ChatMessageSender;
import com.scally.serverutils.undo.UndoManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StairsCommandExecutor implements CommandExecutor, TabCompleter {

    private final ChatMessageSender messageSender;
    private final UndoManager undoManager;

    public StairsCommandExecutor(ChatMessageSender messageSender, UndoManager undoManager) {
        this.messageSender = messageSender;
        this.undoManager = undoManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // TODO: implement
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // TODO: implement
        return null;
    }
}
