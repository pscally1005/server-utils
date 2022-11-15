package com.scally.serverutils.validation;

public class InputValidationException extends RuntimeException {

    private InputValidationErrorCode errorCode;

    public InputValidationException(InputValidationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InputValidationErrorCode getErrorCode() {
        return errorCode;
    }

}
