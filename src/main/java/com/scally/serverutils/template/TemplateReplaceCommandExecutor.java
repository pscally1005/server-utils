package com.scally.serverutils.template;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.DistributionTabCompleter;
import com.scally.serverutils.undo.Change;
import com.scally.serverutils.undo.Changeset;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class TemplateReplaceCommandExecutor<T extends Change> implements CommandExecutor, DistributionTabCompleter {

    private final UndoManager undoManager;

    public TemplateReplaceCommandExecutor(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    protected abstract InputValidator inputValidator();
    protected abstract Changeset<T> changeset();
    public abstract T changeAtLocation(Location location, ValidationResult validationResult);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        // TODO: replace validated() check with throwing exception
        // TODO: catch exception in ServerUtils
        final ValidationResult validationResult = inputValidator().validate(commandSender, args);
        if (!validationResult.validated()) {
            return false;
        }

        final Player player = (Player) commandSender;
        final Coordinates coordinates = validationResult.coordinates();
        final World world = player.getWorld();

        for (int x = coordinates.minX(); x <= coordinates.maxX(); x++) {
            for (int y = coordinates.minY(); y <= coordinates.maxY(); y++) {
                for (int z = coordinates.minZ(); z <= coordinates.maxZ(); z++) {
                    final Location location = new Location(world, x, y, z);
                    final T change = changeAtLocation(location, validationResult);
                    if (change != null)
                        changeset().add(change);
                }
            }
        }

        undoManager.store(player, changeset());
        ChatMessageUtils.sendSuccess(commandSender, String.format("Success! %d blocks changed.", changeset().count()));
        return true;
    }
}
