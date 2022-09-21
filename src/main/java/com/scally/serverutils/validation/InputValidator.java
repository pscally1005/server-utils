package com.scally.serverutils.validation;

import com.scally.serverutils.ServerUtils;
import com.scally.serverutils.chat.ChatMessageUtils;
import org.bukkit.Location;
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

            final int[] coordinateInts = validateCoordinates(commandSender, args);
            if (coordinateInts == null) {
                ChatMessageUtils.sendError(commandSender, "Coordinates must be a valid number!");
                return ValidationResult.invalid();
            }

            final Coordinates coordinates = new Coordinates(coordinateInts);

            if(!validateVolumeSize(coordinates)) {
                ChatMessageUtils.sendError(commandSender, String.format("Volume must be less than %d blocks", ServerUtils.VOLUME_LIMIT));
                return ValidationResult.invalid();
            }

            return new ValidationResult(true, coordinates);
        }

        return new ValidationResult(true, null);
    }

    private boolean validateArgsNumber(String[] args) {
        return expectedNumArgs == args.length;
    }

    private boolean validateCommandSenderType(CommandSender commandSender) {
        return !playerOnly || commandSender instanceof Player;
    }

    private int[] validateCoordinates(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof final Entity entity)) {
            return null;
        }

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

            if(isRelative) {
                if(i == 0 || i == 3) { coords[i] = loc.getBlockX() + coords[i]; }
                else if(i == 1 || i == 4) { coords[i] = loc.getBlockY() + coords[i]; }
                else if(i == 2 || i == 5) { coords[i] = loc.getBlockZ() + coords[i]; }
            }

        }
        return coords;
    }

    public boolean validateVolumeSize(Coordinates coordinates) {
        return coordinates.volume() <= ServerUtils.VOLUME_LIMIT;
    }
}
