package com.scally.serverutils.validation;

import com.scally.serverutils.ServerUtils;

public enum InputValidationErrorCode {
    COMMAND_SENDER_NOT_ENTITY("Command must be sent by an entity!"),
    COMMAND_SENDER_NOT_PLAYER("Command must be sent by a player!"),
    INVALID_ARGS_NUMBER("Invalid number of args!"),
    INVALID_COORDINATES("Coordinates must be valid numbers!"),
    INVALID_DISTRIBUTION_TYPES("Invalid types in distribution!"),
    VOLUME_TOO_LARGE(String.format("Volume must be less than %d blocks!", ServerUtils.VOLUME_LIMIT)),
    WORLDEDIT_NOT_INSTALLED("WorldEdit must be installed and enabled to use 'we'."),
    WORLDEDIT_INCOMPLETE_SELECTION("Set a complete WorldEdit selection first (//pos1, //pos2, or wand)."),
    WORLDEDIT_REQUIRES_PLAYER("WorldEdit selection mode requires a player command sender."),
    WORLDEDIT_SELECTION_FAILED("Could not read your WorldEdit selection.");

    private String message;

    InputValidationErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
