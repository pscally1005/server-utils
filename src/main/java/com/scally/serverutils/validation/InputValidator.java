package com.scally.serverutils.validation;

import com.scally.serverutils.ServerUtils;
import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.Distribution;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InputValidator {

    private final int expectedNumArgs;
    private final boolean playerOnly;
    private final boolean performCoordinateValidation;

    private final int fromDistributionIndex;
    private final Class<? extends BlockData> fromDistributionType;

    private final int toDistributionIndex;
    private final Class<? extends BlockData> toDistributionType;

    private InputValidator(Builder builder) {
        this.expectedNumArgs = builder.expectedNumArgs;
        this.playerOnly = builder.playerOnly;
        this.performCoordinateValidation = builder.performCoordinateValidation;

        this.fromDistributionIndex = builder.fromDistributionIndex;
        this.fromDistributionType = builder.fromDistributionType;

        this.toDistributionIndex = builder.toDistributionIndex;
        this.toDistributionType = builder.toDistributionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int expectedNumArgs;
        private boolean playerOnly;
        private boolean performCoordinateValidation;

        private int fromDistributionIndex;
        private Class<? extends BlockData> fromDistributionType;

        private int toDistributionIndex;
        private Class<? extends BlockData> toDistributionType;

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

        public <T extends BlockData> Builder withFromDistribution(int index, Class<T> type) {
            this.fromDistributionIndex = index;
            this.fromDistributionType = type;
            return this;
        }

        public <T extends BlockData> Builder withToDistribution(int index, Class<T> type) {
            this.toDistributionIndex = index;
            this.toDistributionType = type;
            return this;
        }

        public InputValidator build() {
            return new InputValidator(this);
        }
    }

    public ValidationResult validate(CommandSender commandSender, String[] args) {
        try {
            validateArgsNumber(args);
            validateCommandSenderType(commandSender);
            final Distribution fromDistribution = validateFromDistribution(args);
            final Distribution toDistribution = validateToDistribution(args);
            final Coordinates coordinates = validateCoordinates(commandSender, args);
            validateVolumeSize(coordinates);

            return new ValidationResult(true, coordinates, fromDistribution, toDistribution);
        } catch (InputValidationException exception) {
            ChatMessageUtils.sendError(commandSender, exception.getMessage());
            return ValidationResult.invalid();
        }
    }

    void validateArgsNumber(String[] args) {
        if (expectedNumArgs != args.length)
            throw new InputValidationException("Invalid number of args!");
    }

    void validateCommandSenderType(CommandSender commandSender) {
        if (playerOnly && !(commandSender instanceof Player))
            throw new InputValidationException("Command must be send by a player!");
    }

    Distribution validateFromDistribution(String[] args) {
        if (fromDistributionType == null)
            return null;
        return validateDistribution(args[fromDistributionIndex], fromDistributionType);
    }

    Distribution validateToDistribution(String[] args) {
        if (toDistributionType == null)
            return null;
        return validateDistribution(args[toDistributionIndex], toDistributionType);
    }

    private <T extends BlockData> Distribution validateDistribution(String distributionStr, Class<T> type) {
        final Distribution distribution = Distribution.parse(distributionStr);
        if (distribution == null)
            throw new InputValidationException("Invalid distribution!");

        final boolean hasValidTypes = distribution.isDistributionOf(type);
        if (!hasValidTypes)
            throw new InputValidationException("Invalid types in distribution!");

        return distribution;
    }

    Coordinates validateCoordinates(CommandSender commandSender, String[] args) {
        if (!performCoordinateValidation)
            return null;

        if (!(commandSender instanceof final Entity entity)) {
            throw new InputValidationException("Command must be sent by an entity!");
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
                throw new InputValidationException("Coordinates must be valid numbers!");
            }

            if(isRelative) {
                if(i == 0 || i == 3) { coords[i] = loc.getBlockX() + coords[i]; }
                else if(i == 1 || i == 4) { coords[i] = loc.getBlockY() + coords[i]; }
                else if(i == 2 || i == 5) { coords[i] = loc.getBlockZ() + coords[i]; }
            }

        }
        return new Coordinates(coords);
    }

    void validateVolumeSize(Coordinates coordinates) {
        if (!performCoordinateValidation || coordinates == null)
            return;
        if (coordinates.volume() > ServerUtils.VOLUME_LIMIT) {
            final String message = String.format("Volume must be less than %d blocks", ServerUtils.VOLUME_LIMIT);
            throw new InputValidationException(message);
        }
    }
}
