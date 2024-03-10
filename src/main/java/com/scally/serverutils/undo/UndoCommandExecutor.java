package com.scally.serverutils.undo;

import com.scally.serverutils.distribution.chat.ChatMessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UndoCommandExecutor implements CommandExecutor, TabCompleter {
    private final UndoManager undoManager;

    public UndoCommandExecutor(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player)) {
            ChatMessageUtils.sendError(commandSender, "Must be sent by a player!");
            return false;
        }

        if(strings.length == 0) {
            final Player player = (Player) commandSender;
            return undoManager.undo(player, 1);

        } else if(strings.length == 1) {
            int undoSize;

            try {
                undoSize = Integer.parseInt(strings[0]);
            } catch (NumberFormatException exception) {
                return false;
            }

            final Player player = (Player) commandSender;
            return undoManager.undo(player, undoSize);

        } else {
            ChatMessageUtils.sendError(commandSender, "Invalid number of arguments");
            return false;
        }


    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return List.of();
    }
}