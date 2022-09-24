package com.scally.serverutils.validation;

import com.scally.serverutils.distribution.Distribution;

public record ValidationResult(boolean validated,
                               Coordinates coordinates,
                               Distribution fromDistribution,
                               Distribution toDistribution) {

    public static ValidationResult invalid() {
        return new ValidationResult(false, null, null, null);
    }

}
