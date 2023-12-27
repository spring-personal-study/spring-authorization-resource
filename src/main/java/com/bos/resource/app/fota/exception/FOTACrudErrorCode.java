package com.bos.resource.app.fota.exception;

import com.bos.resource.exception.common.ApiErrorMessage;
import com.bos.resource.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum FOTACrudErrorCode implements ErrorCode {

    FOTA_CRUD_FAIL(BAD_REQUEST, -1401, ApiErrorMessage.FOTA_CRUD_FAIL),
    FIRMWARE_NOT_FOUND(NOT_FOUND, -1402, ApiErrorMessage.FIRMWARE_NOT_FOUND),
    PACKAGE_NOT_FOUND(NOT_FOUND, -1403, ApiErrorMessage.PACKAGE_NOT_FOUND),
    SUPPORT_MODEL_NOT_FOUND(NOT_FOUND, -1404, ApiErrorMessage.SUPPORT_MODEL_NOT_FOUND),
    INCREMENTAL_FIRMWARE_NOT_FOUND(NOT_FOUND, -1405, ApiErrorMessage.INCRMENTAL_FIRMWARE_NOT_FOUND),
    ;

    private static final Map<String, FOTACrudErrorCode> bizCodes =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(FOTACrudErrorCode::getMsg, Function.identity())));
    private final HttpStatus httpStatus;
    private final Integer bizCode;
    private final String msg;

    public Integer findMatchBizCode(final String failMessage) {
        return bizCodes.get(failMessage).getBizCode();
    }

}
