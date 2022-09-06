package com.scally.serverutils.stairs;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StairsCommandExecutor implements CommandExecutor, TabCompleter {

    private final UndoManager undoManager;

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .build();

    public StairsCommandExecutor(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    // /stairs <x1> <y1> <z1> <x2> <y2> <z2> <from-stair> <to-stair>
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final ValidationResult validationResult = inputValidator.validate(commandSender, args);
        if (!validationResult.validated()) {
            return false;
        }

        final Player player = (Player) commandSender;
        final int[] coords = validationResult.coordinates();
        // TODO: move this error message into InputValidator
        // TODO: validate for volume limit
        if(coords == null) {
            ChatMessageUtils.sendError(player, "Coordinates must be a valid number!");
            return false;
        }

        //TODO: Implement rest
        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // TODO: implement
        return null;
    }
}
