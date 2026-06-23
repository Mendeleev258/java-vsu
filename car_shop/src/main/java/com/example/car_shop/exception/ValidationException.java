package com.example.car_shop.exception;

import com.example.car_shop.response.ErrorCode;

public class ValidationException extends RuntimeException {
    private ErrorCode errorCode;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
