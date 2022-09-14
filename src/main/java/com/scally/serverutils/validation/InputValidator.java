package com.scally.serverutils.validation;

import com.scally.serverutils.ServerUtils;
import com.scally.serverutils.chat.ChatMessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InputValidator {

    private final int expectedNumArgs;
    private final boolean playerOnly;
    private final boolean performCoordinateValidation;

    private InputValidator(Builder builder) {
        this.expectedNumArgs = builder.expectedNumArgs;
        this.playerOnly = builder.playerOnly;
        this.performCoordinateValidation = builder.performCoordinateValidation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int expectedNumArgs;
        private boolean playerOnly;
        private boolean performCoordinateValidation;

        public Builder expectedNumArgs(int expectedNumArgs) {
            this.expectedNumArgs = expectedNumArgs;
            return this;
        }

        public Builder playerOnly() {
            this.playerOnly = true;
            return this;
        }

        public Builder withCoordinateValidation() {
            this.playerOnly = true;
            this.performCoordinateValidation = true;
            return this;
        }

        public InputValidator build() {
            return new InputValidator(this);
        }
    }

    public ValidationResult validate(CommandSender commandSender, String[] args) {

        if (!validateArgsNumber(args)) {
            ChatMessageUtils.sendError(commandSender, "Invalid number of args!");
            return ValidationResult.invalid();
        }

        if (!validateCommandSenderType(commandSender)) {
            ChatMessageUtils.sendError(commandSender,"Command must be sent by a player!");
            return ValidationResult.invalid();
        }

        if (performCoordinateValidation) {

            final int[] coordinates = validateCoordinates(commandSender, args);

            if (coordinates == null) {
                ChatMessageUtils.sendError(commandSender, "Coordinates must be a valid number!");
                return ValidationResult.invalid();
            }

            if(!validateVolumeSize(coordinates)) {
                ChatMessageUtils.sendError(commandSender, String.format("Volume must be less than %d blocks", ServerUtils.VOLUME_LIMIT));
                return ValidationResult.invalid();
            }

            return new ValidationResult(true, coordinates);
        }

        return new ValidationResult(true, null);
    }

    private boolean validateArgsNumber(String[] args) {
        if (expectedNumArgs != args.length) {
            return false;
        }
        return true;
    }

    private boolean validateCommandSenderType(CommandSender commandSender) {
        if (playerOnly && !(commandSender instanceof Player)) {
            return false;
        }
        return true;
    }

    private int[] validateCoordinates(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Entity)) {
            return null;
        }
        final Entity entity = (Entity) commandSender;

        int[] coords = new int[6];
        final Location loc = entity.getLocation();
        for (int i = 0; i < coords.length; i++) {
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

    public boolean validateVolumeSize(int[] coords) {

        final int x1 = coords[0];
        final int y1 = coords[1];
        final int z1 = coords[2];

        final int x2 = coords[3];
        final int y2 = coords[4];
        final int z2 = coords[5];

        final long volume = Math.abs(x2 - x1) * Math.abs(y2 - y1) * Math.abs(z2 - z1);
        if (volume > ServerUtils.VOLUME_LIMIT) {
            return false;
        }

        return true;

    }
}
