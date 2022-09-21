package com.scally.serverutils.validation;

public record ValidationResult(boolean validated,
                               Coordinates coordinates) {

    public static ValidationResult invalid() {
        return new ValidationResult(false, null);
    }

}
