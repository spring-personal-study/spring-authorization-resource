package com.bos.resource.app.fota.exception;



import com.bos.resource.exception.common.ErrorCode;
import com.bos.resource.exception.common.InvalidParameterException;
import org.springframework.validation.Errors;

public class InvalidFOTAParameterException extends InvalidParameterException {
    public InvalidFOTAParameterException(Errors errors, ErrorCode errorCode) {
        super(errors, errorCode);
    }
}
