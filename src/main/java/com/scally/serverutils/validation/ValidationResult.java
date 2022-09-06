package com.scally.serverutils.validation;

public record ValidationResult(boolean validated,
                               int[] coordinates) {

    public static ValidationResult invalid() {
        return new ValidationResult(false, null);
    }

}
