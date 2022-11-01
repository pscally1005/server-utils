package com.scally.serverutils.validation;

import com.scally.serverutils.ServerUtils;

public enum InputValidationErrorCode {
    COMMAND_SENDER_NOT_ENTITY("Command must be sent by an entity!"),
    COMMAND_SENDER_NOT_PLAYER("Command must be sent by a player!"),
    INVALID_ARGS_NUMBER("Invalid number of args!"),
    INVALID_COORDINATES("Coordinates must be valid numbers!"),
    INVALID_DISTRIBUTION_TYPES("Invalid types in distribution!"),
    VOLUME_TOO_LARGE(String.format("Volume must be less than %d blocks!", ServerUtils.VOLUME_LIMIT));

    private String message;

    InputValidationErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
