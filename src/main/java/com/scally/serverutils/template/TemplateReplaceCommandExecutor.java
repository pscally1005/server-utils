package com.scally.serverutils.template;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.DistributionTabCompleter;
import com.scally.serverutils.undo.Change;
import com.scally.serverutils.undo.Changeset;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.InputValidationException;
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

    protected TemplateReplaceCommandExecutor(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    protected abstract InputValidator inputValidator();
    protected abstract Changeset<T> newChangeset();
    public abstract T changeAtLocation(Location location, ValidationResult validationResult);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        ValidationResult validationResult;
        try {
            validationResult = inputValidator().validate(commandSender, args);
        } catch (InputValidationException e) {
            return false;
        }

        final Player player = (Player) commandSender;
        final Coordinates coordinates = validationResult.coordinates();
        final World world = player.getWorld();

        Changeset<T> changeset = newChangeset();
        for (int x = coordinates.minX(); x <= coordinates.maxX(); x++) {
            for (int y = coordinates.minY(); y <= coordinates.maxY(); y++) {
                for (int z = coordinates.minZ(); z <= coordinates.maxZ(); z++) {
                    final Location location = new Location(world, x, y, z);
                    final T change = changeAtLocation(location, validationResult);
                    if (change != null)
                        changeset.add(change);
                }
            }
        }

        if(changeset.count() > 0) {
            undoManager.store(player, changeset);
        }
        ChatMessageUtils.sendSuccess(commandSender, String.format("Success! %d blocks changed.", changeset.count()));
        return true;
    }
}
