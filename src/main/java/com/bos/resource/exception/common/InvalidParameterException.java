package com.bos.resource.exception.common;

import org.springframework.validation.Errors;

public abstract class InvalidParameterException extends BizException {

    private final Errors errors;
    private final ErrorCode errorCode;

    public InvalidParameterException(Errors errors, ErrorCode errorCode) {
        super(GeneralParameterErrorCode.INVALID_PARAMETER);
        this.errors = errors;
        this.errorCode = errorCode;
    }

    public Errors getErrors() {
        return errors;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
